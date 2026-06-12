package com.needhelp.aips.controller;

import com.needhelp.aips.entity.Category;
import com.needhelp.aips.entity.Medicine;
import com.needhelp.aips.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 药品接口集成测试。
 */
@SpringBootTest
@ActiveProfiles("test")
class DrugControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        Category cat1 = new Category();
        cat1.setParentId(0L);
        cat1.setName("感冒/发烧");
        cat1.setSortOrder(1);
        cat1.setStatus(1);
        cat1 = categoryRepository.save(cat1);

        Category cat2 = new Category();
        cat2.setParentId(0L);
        cat2.setName("慢性病");
        cat2.setSortOrder(2);
        cat2.setStatus(1);
        categoryRepository.save(cat2);

        Medicine med1 = new Medicine();
        med1.setCategory(cat1);
        med1.setName("布洛芬缓释胶囊");
        med1.setGenericName("Ibuprofen");
        med1.setSpecification("0.3g*20粒");
        med1.setManufacturer("中美史克");
        med1.setIsPrescription(0);
        med1.setPrice(new BigDecimal("19.90"));
        med1.setOriginalPrice(new BigDecimal("25.00"));
        med1.setStock(100);
        med1.setIndications("用于缓解轻至中度疼痛");
        med1.setDosage("口服，一次1粒，一日2次");
        med1.setContraindications("对本品过敏者禁用");
        med1.setStatus(1);
        medicineRepository.save(med1);
    }

    @Test
    void testGetCategories() throws Exception {
        mockMvc.perform(get("/api/v1/drug/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(greaterThan(0)))
                .andExpect(jsonPath("$.data[0].name").value("感冒/发烧"));
    }

    @Test
    void testSearchDrugs() throws Exception {
        mockMvc.perform(get("/api/v1/drug/search")
                        .param("keyword", "布洛芬")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.list[0].name").value("布洛芬缓释胶囊"));
    }

    @Test
    void testGetDrugDetail() throws Exception {
        mockMvc.perform(get("/api/v1/drug/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("布洛芬缓释胶囊"));
    }

    @Test
    void testGetDrugNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/drug/99999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }
}
