package org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.daos;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.entities.Province;
import org.iesalixar.daw2.rubenruiz.dwese2627_ticket_logger_webapp.entities.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@Transactional
// Indica que todos los métodos de esta clase deben ejecutarse dentro de una transacción.
// Si algún método lanza una excepción en tiempo de ejecución, la transacción se revierte automáticamente (rollback).
// Esto asegura la integridad de los datos: todas las operaciones dentro del método se confirman o se cancelan juntas.
public class ProvinceDAOImpl implements ProvinceDAO {

    private static final Logger logger = LoggerFactory.getLogger(ProvinceDAOImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Lista todas las provincias de la base de datos (con su región asociada).
     * @return Lista de provincias con regiones.
     */
    @Override
    public List<Province> listAllProvinces() {
        logger.info("Listing all provinces with their regions from the database.");
        String hql = "SELECT p FROM Province p JOIN FETCH p.region";
        List<Province> provinces = entityManager.createQuery(hql, Province.class).getResultList();
        logger.info("Retrieved {} provinces from the database.", provinces.size());
        return provinces;
    }

    /**
     * Verifica si existe una provincia con el código especificado.
     * @param code código de la provincia a verificar.
     * @return true si existe, false en caso contrario.
     */
    /**
     * Verifica si existe una provincia con el código especificado.
     * @param code código de la provincia a verificar.
     * @return true si existe, false en caso contrario.
     */
    public boolean existsProvinceByCode(String code) {
        logger.info("Checking if province with code: {} exists", code);
        String hql = "SELECT COUNT(p) FROM Province p WHERE UPPER(p.code) = :code";
        Long count = entityManager.createQuery(hql, Long.class)
                .setParameter("code", code.toUpperCase())
                .getSingleResult();
        boolean exists = count != null && count > 0;
        logger.info("Province with code: {} exists: {}", code, exists);

        return exists;
    }

    /**
     * Verifica si existe una provincia con el código especificado,
     * excluyendo una provincia con un ID específico.
     * @param code código de la provincia a verificar.
     * @param id   ID de la provincia a excluir.
     * @return true si existe otra provincia con ese código, false en caso contrario.
     */
    public boolean existsProvinceByCodeAndNotId(String code, Long id) {
        logger.info("Checking if province with code: {} exists excluding id: {}", code, id);
        String query = "SELECT COUNT(p) FROM Province p WHERE UPPER(p.code) = :code AND p.id != :id";
        Long count = entityManager.createQuery(query, Long.class)
                .setParameter("code", code.toUpperCase())
                .setParameter("id", id)
                .getSingleResult();
        boolean exists = count != null && count > 0;
        logger.info("Province with code: {} exists excluding id {}: {}", code, id, exists);
        return exists;
    }

    /**
     * Inserta una nueva provincia en la base de datos.
     * @param province Provincia a insertar (debe incluir región con id).
     */
    /**
     * Inserta una nueva provincia en la base de datos.
     * @param province Provincia a insertar (debe incluir región con id).
     */
    @Override
    public void insertProvince(Province province) {
        logger.info("Inserting province with code: {}, name: {}, regionId: {}",
                province.getCode(),
                province.getName(),
                province.getRegion() != null ? province.getRegion().getId() : null
        );
        entityManager.persist(province);
        logger.info("Inserted province with ID: {}", province.getId());
    }

    /**
     * Recupera una provincia por su ID (incluyendo su región).
     * @param id ID de la provincia a recuperar
     * @return Provincia encontrada (con su región) o null si no existe
     */
    public Province getProvinceById(Long id) {
        logger.info("Retrieving province by id: {}", id);
        Province province = entityManager.find(Province.class, id);
        if (province != null) {
            logger.info("Province retrieved: {} - {}", province.getCode(), province.getName());
        } else {
            logger.warn("No province found with id: {}", id);
        }
        return province;
    }

    /**
     * Actualiza una provincia existente en la base de datos.
     * @param province Provincia a actualizar (debe incluir id y región con id).
     */
    @Override
    public void updateProvince(Province province) {
        logger.info("Updating province with id: {}", province.getId());
        entityManager.merge(province);
        logger.info("Updated province with id: {}", province.getId());
    }

    /**
     * Elimina una provincia de la base de datos.
     * @param id ID de la provincia a eliminar
     */
    /**
     * Elimina una provincia de la base de datos.
     * @param id ID de la provincia a eliminar
     */
    @Override
    public void deleteProvince(Long id) {
        logger.info("Deleting province with id: {}", id);
        Province province = entityManager.find(Province.class, id);
        if (province != null) {
            entityManager.remove(province);
            logger.info("Deleted province with id: {}", id);
        } else {
            logger.warn("Province with id: {} not found.", id);
        }
    }

    /**
     * Lista una página de provincias con su región asociada.
     *
     * @param page página actual (0-based)
     * @param size número de elementos por página
     * @return lista de provincias para esa página
     */
    /**
     * Lista una página de provincias con su región asociada.
     *
     * @param page      página actual (0-based)
     * @param size      número de elementos por página
     * @param sortField campo por el que se ordenan los resultados (id, code, name, regionName).
     * @param sortDir   dirección de ordenación ("asc" o "desc").
     * @return lista de provincias para esa página
     */
    @Override
    public List<Province> listProvincesPage(int page, int size, String sortField, String sortDir) {
        logger.info("Listing provinces page={}, size={}, sortField={}, sortDir={} from the database.",
                page, size, sortField, sortDir);
        int offset = page * size;

        // 1. Construcción de Criteria
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Province> cq = cb.createQuery(Province.class);
        Root<Province> root = cq.from(Province.class);

        // Hacemos fetch de region para que venga cargada (equivalente al JOIN FETCH)
        root.fetch("region", JoinType.INNER);
        // Join separado para poder usar region.name en ORDER BY
        Join<Province, Region> regionJoin = root.join("region", JoinType.INNER);

        // 2. Determinar el campo de ordenación permitido (whitelist)
        Path<?> sortPath;
        switch (sortField) {
            case "id"         -> sortPath = root.get("id");
            case "code"       -> sortPath = root.get("code");
            case "name"       -> sortPath = root.get("name");
            case "regionName" -> sortPath = regionJoin.get("name"); // region.name
            default           -> {
                logger.warn("Unknown sortField '{}' for Province, defaulting to 'name'.", sortField);
                sortPath = root.get("name");
            }
        }

        // 3. Dirección de ordenación
        boolean descending = "desc".equalsIgnoreCase(sortDir);
        Order order = descending ? cb.desc(sortPath) : cb.asc(sortPath);

        // 4. Aplicar ordenación a la query
        cq.select(root).orderBy(order);

        // 5. Crear TypedQuery, aplicar paginación y ejecutar
        return entityManager.createQuery(cq)
                .setFirstResult(offset)
                .setMaxResults(size)
                .getResultList();
    }

    /**
     * Devuelve el número total de provincias.
     */
    @Override
    public long countProvinces() {
        String hql = "SELECT COUNT(p) FROM Province p";
        Long total = entityManager.createQuery(hql, Long.class).getSingleResult();
        return (total != null) ? total : 0L;
    }

}