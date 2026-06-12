package com.needhelp.aips.service.user;

import com.needhelp.aips.common.exception.BusinessException;
import com.needhelp.aips.dto.user.HealthProfileResponse;
import com.needhelp.aips.dto.user.ReminderRequest;
import com.needhelp.aips.entity.*;
import com.needhelp.aips.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户服务。
 * 处理健康档案查询、用药提醒设置、地址管理等功能。
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final HealthRecordRepository healthRecordRepository;
    private final MedicineReminderRepository reminderRepository;
    private final FamilyMemberRepository familyMemberRepository;

    public UserService(UserRepository userRepository,
                       HealthRecordRepository healthRecordRepository,
                       MedicineReminderRepository reminderRepository,
                       FamilyMemberRepository familyMemberRepository) {
        this.userRepository = userRepository;
        this.healthRecordRepository = healthRecordRepository;
        this.reminderRepository = reminderRepository;
        this.familyMemberRepository = familyMemberRepository;
    }

    /**
     * 获取用户健康档案。
     */
    public HealthProfileResponse getHealthProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));

        HealthProfileResponse profile = new HealthProfileResponse();

        // 过敏信息和慢性病从家庭成员表获取（主用户本人）
        List<FamilyMember> members = familyMemberRepository.findByUserId(userId);
        // 取第一个家庭成员的健康信息作为示例
        if (!members.isEmpty()) {
            FamilyMember self = members.get(0);
            profile.setAllergyInfo(self.getAllergyInfo());
            profile.setChronicDiseases(self.getChronicDiseases());
        }

        // 最新血压
        healthRecordRepository.findTopByUserIdAndRecordTypeOrderByRecordDateDesc(userId, 1)
                .ifPresent(hr -> {
                    HealthProfileResponse.BloodPressure bp = new HealthProfileResponse.BloodPressure();
                    bp.setSystolic(hr.getSystolic());
                    bp.setDiastolic(hr.getDiastolic());
                    profile.setLatestBloodPressure(bp);
                });

        // 最新血糖
        healthRecordRepository.findTopByUserIdAndRecordTypeOrderByRecordDateDesc(userId, 2)
                .ifPresent(hr -> {
                    HealthProfileResponse.BloodGlucose bg = new HealthProfileResponse.BloodGlucose();
                    bg.setValue(hr.getBloodSugar());
                    bg.setType(hr.getSugarType() != null && hr.getSugarType() == 1 ? "空腹" : "餐后");
                    profile.setLatestBloodGlucose(bg);
                });

        // 家庭成员
        List<HealthProfileResponse.FamilyMemberInfo> fmList = members.stream().map(m -> {
            HealthProfileResponse.FamilyMemberInfo info = new HealthProfileResponse.FamilyMemberInfo();
            info.setName(m.getName());
            info.setRelationship(mapRelationship(m.getRelationship()));
            return info;
        }).collect(Collectors.toList());
        profile.setFamilyMembers(fmList);

        // 当前用药（从启用的提醒中获取）
        List<MedicineReminder> reminders = reminderRepository.findByUserIdAndIsEnabled(userId, 1);
        List<HealthProfileResponse.CurrentMedication> meds = reminders.stream().map(r -> {
            HealthProfileResponse.CurrentMedication cm = new HealthProfileResponse.CurrentMedication();
            cm.setName(r.getMedicineName());
            cm.setDosage(r.getDosage());
            return cm;
        }).collect(Collectors.toList());
        profile.setCurrentMedications(meds);

        return profile;
    }

    /**
     * 设置用药提醒。
     */
    public String setReminder(Long userId, ReminderRequest request) {
        // 查找是否有已存在的提醒
        List<MedicineReminder> existing = reminderRepository.findByUserIdAndIsEnabled(userId, 1);
        Optional<MedicineReminder> found = existing.stream()
                .filter(r -> r.getMedicineId().equals(request.getMedicineId()))
                .findFirst();

        MedicineReminder reminder;
        if (found.isPresent()) {
            reminder = found.get();
        } else {
            reminder = new MedicineReminder();
            reminder.setUserId(userId);
            reminder.setMedicineId(request.getMedicineId());
        }

        reminder.setMedicineName(request.getMedicineName());
        reminder.setDosage(request.getDosage());
        reminder.setRemindTime(LocalTime.parse(request.getRemindTime()));
        reminder.setRepeatDays(request.getRepeatDays() != null ? request.getRepeatDays() : "1,2,3,4,5,6,7");
        reminder.setIsEnabled(request.getIsEnabled() != null ? request.getIsEnabled() : 1);

        reminderRepository.save(reminder);
        return "用药提醒设置成功";
    }

    private String mapRelationship(Integer code) {
        if (code == null) return "其他";
        return switch (code) {
            case 1 -> "配偶";
            case 2 -> "父亲";
            case 3 -> "母亲";
            case 4 -> "儿子";
            case 5 -> "女儿";
            default -> "其他";
        };
    }
}
