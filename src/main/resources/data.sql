-- ============================================================
-- AIPS 初始数据
-- ============================================================

-- 药品分类
INSERT INTO categories (parent_id, name, icon_url, sort_order, status, create_time) VALUES
(0, '感冒/发烧', '/icons/cold.png', 1, 1, NOW()),
(0, '慢性病', '/icons/chronic.png', 2, 1, NOW()),
(0, '维生素/保健品', '/icons/vitamin.png', 3, 1, NOW()),
(0, '消化系统', '/icons/digest.png', 4, 1, NOW()),
(0, '皮肤外用', '/icons/skin.png', 5, 1, NOW()),
(0, '儿科用药', '/icons/child.png', 6, 1, NOW()),
(0, '妇科用药', '/icons/women.png', 7, 1, NOW()),
(0, '男科用药', '/icons/men.png', 8, 1, NOW());

-- 子分类
INSERT INTO categories (parent_id, name, icon_url, sort_order, status, create_time) VALUES
(1, '感冒药', '/icons/cold-med.png', 1, 1, NOW()),
(1, '退烧药', '/icons/fever.png', 2, 1, NOW()),
(2, '降压药', '/icons/bp.png', 1, 1, NOW()),
(2, '降糖药', '/icons/diabetes.png', 2, 1, NOW()),
(3, '维生素C', '/icons/vc.png', 1, 1, NOW()),
(3, '钙片', '/icons/calcium.png', 2, 1, NOW());

-- 示例药品
INSERT INTO medicines (category_id, name, generic_name, specification, manufacturer, is_prescription, price, original_price, stock, main_image_url, indications, dosage, contraindications, status, create_time, update_time) VALUES
(1, '布洛芬缓释胶囊', 'Ibuprofen', '0.3g*20粒', '中美天津史克制药有限公司', 0, 19.90, 25.00, 500, '/images/ibuprofen.jpg', '用于缓解轻至中度疼痛，如头痛、关节痛、牙痛等。也可用于普通感冒或流感引起的发热。', '口服，成人一次1粒，一日2次（早晚各一次）', '对本品过敏者禁用；活动性消化道溃疡患者禁用；孕妇及哺乳期妇女禁用', 1, NOW(), NOW()),
(1, '对乙酰氨基酚片', 'Paracetamol', '500mg*10片', '强生制药有限公司', 0, 12.50, 15.00, 300, '/images/paracetamol.jpg', '用于普通感冒或流感引起的发热，也用于缓解轻至中度疼痛', '口服，成人一次1片，若持续发热或疼痛可间隔4-6小时重复用药一次', '严重肝肾功能不全者禁用；对本品过敏者禁用', 1, NOW(), NOW()),
(2, '氨氯地平贝那普利片', 'Amlodipine Besylate', '5mg*30片', '诺华制药有限公司', 1, 35.00, 42.00, 200, '/images/amlodipine.jpg', '用于治疗高血压', '口服，每日1次，每次1片', '对本品过敏者禁用；主动脉狭窄患者慎用', 1, NOW(), NOW()),
(2, '二甲双胍缓释片', 'Metformin', '500mg*60片', '中美上海施贵宝制药有限公司', 1, 28.80, 35.00, 150, '/images/metformin.jpg', '用于2型糖尿病的治疗', '口服，起始剂量为500mg每日一次，随餐服用', '肾功能不全者慎用；严重肝病患者禁用', 1, NOW(), NOW()),
(3, '维生素C泡腾片', 'Vitamin C', '1g*20片', '拜耳医药保健有限公司', 0, 45.00, 55.00, 400, '/images/vitc.jpg', '用于预防和治疗维生素C缺乏症，增强免疫力', '一日1片，放入水中溶解后服用', '高草酸尿症患者慎用', 1, NOW(), NOW()),
(4, '蒙脱石散', 'Montmorillonite Powder', '3g*10袋', '扬子江药业集团有限公司', 0, 15.50, 18.00, 600, '/images/montmorillonite.jpg', '用于成人及儿童急、慢性腹泻', '口服，成人一次1袋，一日3次', '对本品过敏者禁用', 1, NOW(), NOW()),
(4, '奥美拉唑肠溶胶囊', 'Omeprazole', '20mg*14粒', '阿斯利康制药有限公司', 0, 28.00, 35.00, 250, '/images/omeprazole.jpg', '用于胃酸过多引起的烧心和反酸症状', '口服，每日1次，每次1粒', '对本品过敏者禁用', 1, NOW(), NOW()),
(5, '红霉素软膏', 'Erythromycin Ointment', '10g/支', '广州白云山制药总厂', 0, 8.90, 10.00, 800, '/images/erythromycin.jpg', '用于脓疱疮等化脓性皮肤病及烧伤、溃疡面的感染', '局部外用，取适量涂于患处，一日2次', '对本品过敏者禁用', 1, NOW(), NOW());

-- 示例药师
INSERT INTO pharmacists (name, license_no, phone, status, create_time) VALUES
('李药师', 'PH20250001', '13900139001', 1, NOW()),
('王药师', 'PH20250002', '13900139002', 1, NOW()),
('张药师', 'PH20250003', '13900139003', 2, NOW());

-- 示例横幅
INSERT INTO banners (title, image_url, link_url, sort_order, status, start_time, end_time, create_time) VALUES
('夏季健康季 全场满199减50', '/banners/summer-sale.jpg', '/promotions/summer', 1, 1, '2025-06-01 00:00:00', '2025-08-31 23:59:59', NOW()),
('新用户专享 首单包邮', '/banners/new-user.jpg', '/promotions/new-user', 2, 1, '2025-01-01 00:00:00', '2025-12-31 23:59:59', NOW());
