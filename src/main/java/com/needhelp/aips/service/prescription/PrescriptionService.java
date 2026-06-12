package com.needhelp.aips.service.prescription;

import com.needhelp.aips.common.exception.BusinessException;
import com.needhelp.aips.dto.prescription.PrescriptionStatusResponse;
import com.needhelp.aips.dto.prescription.PrescriptionUploadResponse;
import com.needhelp.aips.entity.Prescription;
import com.needhelp.aips.entity.PrescriptionItem;
import com.needhelp.aips.repository.PrescriptionItemRepository;
import com.needhelp.aips.repository.PrescriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 处方服务。
 * 处理处方的上传、模拟 OCR 识别、AI 配伍检查、状态查询。
 * 注：OCR 和 AI 检查为模拟实现。
 */
@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionItemRepository itemRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository,
                               PrescriptionItemRepository itemRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.itemRepository = itemRepository;
    }

    /**
     * 上传处方图片，启动模拟 OCR 处理。
     */
    @Transactional
    public PrescriptionUploadResponse upload(Long userId, String imageUrl) {
        Prescription p = new Prescription();
        p.setUserId(userId);
        p.setPrescriptionNo("RX" + System.currentTimeMillis() + new Random().nextInt(1000));
        p.setImageUrl(imageUrl);
        p.setOcrStatus(0);  // 待识别
        p.setAiCheckStatus(0);
        p.setPharmacistStatus(0);
        p = prescriptionRepository.save(p);

        // 模拟 OCR 处理
        simulateOcr(p);

        PrescriptionUploadResponse resp = new PrescriptionUploadResponse();
        resp.setPrescriptionId(p.getId());
        resp.setImageUrl(p.getImageUrl());
        resp.setOcrStatus(p.getOcrStatus());
        resp.setMessage("处方上传成功，正在识别中...");
        return resp;
    }

    /**
     * 查询处方状态和详情。
     */
    public PrescriptionStatusResponse getStatus(Long prescriptionId) {
        Prescription p = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new BusinessException(404, "处方不存在"));

        PrescriptionStatusResponse resp = new PrescriptionStatusResponse();
        resp.setPrescriptionId(p.getId());
        resp.setImageUrl(p.getImageUrl());
        resp.setHospitalName(p.getHospitalName());
        resp.setDoctorName(p.getDoctorName());
        resp.setPharmacistComment(p.getPharmacistComment());

        // 整体状态映射
        if (p.getOcrStatus() == 0) resp.setStatus(1);   // 待上传/待识别
        else if (p.getOcrStatus() == 1) resp.setStatus(1); // 识别中
        else if (p.getAiCheckStatus() == 0) resp.setStatus(2); // AI检查中
        else if (p.getPharmacistStatus() == 0) resp.setStatus(3); // 待药师审核
        else if (p.getPharmacistStatus() == 1) resp.setStatus(4); // 已通过
        else if (p.getPharmacistStatus() == 2) resp.setStatus(5); // 已驳回
        else resp.setStatus(6); // 需重传

        // 明细
        List<PrescriptionItem> items = itemRepository.findByPrescriptionId(prescriptionId);
        List<PrescriptionStatusResponse.PrescriptionItemInfo> itemList = items.stream().map(i -> {
            PrescriptionStatusResponse.PrescriptionItemInfo info =
                    new PrescriptionStatusResponse.PrescriptionItemInfo();
            info.setMedicineName(i.getMedicineName());
            info.setSpecification(i.getSpecification());
            info.setDosage(i.getDosage());
            info.setQuantity(i.getQuantity());
            return info;
        }).collect(Collectors.toList());
        resp.setItems(itemList);

        return resp;
    }

    /**
     * 模拟 OCR 识别和 AI 检查流程。
     * 实际生产环境应接入真实 OCR 服务和 AI 模型。
     */
    private void simulateOcr(Prescription prescription) {
        // 模拟 OCR 识别
        prescription.setOcrStatus(2); // 识别成功
        prescription.setHospitalName("市人民医院");
        prescription.setDoctorName("张医生");
        prescription.setPrescriptionDate(LocalDate.now().minusDays(1));
        prescription.setAiCheckStatus(1); // AI检查通过
        prescriptionRepository.save(prescription);

        // 插入模拟处方明细
        String[][] mockItems = {
            {"氨氯地平片", "5mg", "每日一次", "1"},
            {"二甲双胍", "500mg", "每日两次", "2"}
        };
        for (String[] data : mockItems) {
            PrescriptionItem item = new PrescriptionItem();
            item.setPrescription(prescription);
            item.setMedicineName(data[0]);
            item.setSpecification(data[1]);
            item.setDosage(data[2]);
            item.setQuantity(Integer.parseInt(data[3]));
            item.setAiCheckResult(1);
            itemRepository.save(item);
        }
    }
}
