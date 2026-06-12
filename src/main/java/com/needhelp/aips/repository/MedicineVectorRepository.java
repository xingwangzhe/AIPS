package com.needhelp.aips.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class MedicineVectorRepository {

    private static final Logger log = LoggerFactory.getLogger(MedicineVectorRepository.class);
    private final JdbcTemplate jdbc;
    public MedicineVectorRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public void saveEmbedding(Long medicineId, float[] vec) {
        try { jdbc.update("UPDATE medicines SET embedding = ?::vector WHERE id = ?", vectorString(vec), medicineId); }
        catch (Exception e) { log.debug("saveEmbedding 跳过: {}", e.getMessage()); }
    }

    public boolean hasEmbedding(Long medicineId) {
        try { Integer c = jdbc.queryForObject("SELECT COUNT(*) FROM medicines WHERE id = ? AND embedding IS NOT NULL", Integer.class, medicineId); return c != null && c > 0; }
        catch (Exception e) { return false; }
    }

    public List<Long> searchSimilar(float[] queryVec, int limit) {
        try { return jdbc.queryForList("SELECT id FROM medicines WHERE embedding IS NOT NULL AND status = 1 ORDER BY embedding <=> ?::vector LIMIT ?", Long.class, vectorString(queryVec), limit); }
        catch (Exception e) { log.debug("searchSimilar 跳过: {}", e.getMessage()); return List.of(); }
    }

    public long countUnindexed() {
        try { Long c = jdbc.queryForObject("SELECT COUNT(*) FROM medicines WHERE status = 1 AND embedding IS NULL", Long.class); return c != null ? c : 0; }
        catch (Exception e) { return 0; }
    }

    private String vectorString(float[] vec) { return Arrays.toString(vec).replace("[", "{").replace("]", "}"); }
}
