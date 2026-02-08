package app.onestepcloser.blog.utility;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.util.Pair;
import org.springframework.util.CollectionUtils;

import javax.management.Query;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class QueryBuilder {
    private final LinkedList<String> fields = new LinkedList<>();
    private String tableName;
    private Class<?> table;
    private Class<?> entity;
    private Criteria criteria;
    private Map<String, String> aliases;
    private final Map<String, Object> params = new HashMap<>();
    private int paramIndex = 0;
    private boolean unknownEntity = true;
    private Map<Pair<String, String>, Pair<JoinType, QueryBuilder>> joinMap;
    private LinkedList<Pair<String, String>> groupBy;
    private LinkedList<Pair<String, String>> orderBy;
    private List<String> foreignFields;

    private final String ALIAS = "<ALIAS>";
    private final String COUNT = "<COUNT>";
    private final String PARAM = "param";

//    @PersistenceContext
//    private EntityManager entityManager;
//
//    public QueryBuilder(EntityManager entityManager) {
//        this.entityManager = entityManager;
//    }

    public QueryBuilder(Class<?> entity) {
        this.from(entity);
    }

    public void setEntity(Class<?> entity) {
        this.entity = entity;
        customizeFields(false);
    }

    public void customizeFields(boolean value) {
        this.unknownEntity = value;
    }

    public QueryBuilder select(String... fields) {
        Collections.addAll(this.fields, fields);
        return this;
    }

    public QueryBuilder select(String field) {
        if (this.aliases == null) this.aliases = new HashMap<>();
        this.fields.add(field);
        aliases.put(this.ALIAS, field);
        return this;
    }

    public QueryBuilder selectForeignFields(Class<?> entity, String... fields) {
        if (this.foreignFields == null) this.foreignFields = new ArrayList<>();
        String tableName = AppUtil.convertCamelToSnakeStyle(entity.getSimpleName());
        for (String field : fields) {
            String fieldName = AppUtil.convertCamelToSnakeStyle(field);
            this.foreignFields.add(tableName.concat(Charactor.DOT.value).concat(fieldName));
        }
        return this;
    }

    public QueryBuilder count() {
        this.fields.add(COUNT);
        return this;
    }

    public QueryBuilder selectConcat(String... strings) {
        StringBuilder sql = new StringBuilder("concat(");
        for (String string : strings) {
            if (string != null && string.equals(Charactor.BLANK.value)) {
                sql.append("'").append(string).append("'").append(",");
            }
            else {
                sql.append(string).append(",");
            }
        }
        return select(sql.append(")").toString().replace(",)", ")"));
    }

    public QueryBuilder as(String alias) {
        if (this.aliases == null) {
            this.aliases = new HashMap<>();
        }
        String field = aliases.get(this.ALIAS);
        if (StringUtils.isBlank(field)) return this;
        aliases.put(AppUtil.convertCamelToSnakeStyle(field), alias);
        return this;
    }

    public QueryBuilder from(Class<?> entity) {
        this.table = entity;
        this.tableName = AppUtil.convertCamelToSnakeStyle(this.table.getSimpleName());
        return this;
    }

    public QueryBuilder where(Criteria criteria) {
        this.criteria = criteria;
        return this;
    }

    public void groupBy(Class<?> clazz, String groupBy) {
        if (CollectionUtils.isEmpty(this.groupBy)) {
            this.groupBy = new LinkedList<>();
        }
        String tableName = AppUtil.convertCamelToSnakeStyle(clazz.getSimpleName());
        String fieldName = AppUtil.convertCamelToSnakeStyle(groupBy);
        this.groupBy.add(Pair.of(tableName, fieldName));
    }

    public void orderBy(Class<?> clazz, String orderBy) {
        orderBy(clazz, orderBy, "ASC");
    }

    public void orderBy(Class<?> clazz, String orderBy, String sort) {
        if (CollectionUtils.isEmpty(this.orderBy)) {
            this.orderBy = new LinkedList<>();
        }
        String tableName = AppUtil.convertCamelToSnakeStyle(clazz.getSimpleName());
        StringBuilder fieldName = new StringBuilder(AppUtil.convertCamelToSnakeStyle(orderBy));
        if (sort.equals("DESC")) {
            fieldName.append(Charactor.BLANK.value).append(sort);
        }
        this.orderBy.add(Pair.of(tableName, fieldName.toString()));
    }

    public void orderByRandom() {
        if (CollectionUtils.isEmpty(this.orderBy)) {
            this.orderBy = new LinkedList<>();
        }
        this.orderBy.add(Pair.of(Charactor.EMPTY.value, "RAND()"));
    }

    public QueryBuilder innerJoin(QueryBuilder joinedQuery, String key, String foreign) {
        join(joinedQuery, key, foreign, JoinType.INNER_JOIN);
        return joinedQuery;
    }

    public QueryBuilder leftJoin(QueryBuilder joinedQuery, String key, String foreign) {
        join(joinedQuery, key, foreign, JoinType.LEFT_JOIN);
        return joinedQuery;
    }

    public QueryBuilder rightJoin(QueryBuilder joinedQuery, String key, String foreign) {
        join(joinedQuery, key, foreign, JoinType.RIGHT_JOIN);
        return joinedQuery;
    }

    public QueryBuilder fullJoin(QueryBuilder joinedQuery, String key, String foreign) {
        join(joinedQuery, key, foreign, JoinType.FULL_JOIN);
        return joinedQuery;
    }

    public QueryBuilder innerJoin(Class<?> clazz, String key, String foreign) {
        QueryBuilder joinedQuery = new QueryBuilder(clazz);
        join(joinedQuery, key, foreign, JoinType.INNER_JOIN);
        return joinedQuery;
    }

    public QueryBuilder leftJoin(Class<?> clazz, String key, String foreign) {
        QueryBuilder joinedQuery = new QueryBuilder(clazz);
        join(joinedQuery, key, foreign, JoinType.LEFT_JOIN);
        return joinedQuery;
    }

    public QueryBuilder rightJoin(Class<?> clazz, String key, String foreign) {
        QueryBuilder joinedQuery = new QueryBuilder(clazz);
        join(joinedQuery, key, foreign, JoinType.RIGHT_JOIN);
        return joinedQuery;
    }

    public QueryBuilder fullJoin(Class<?> clazz, String key, String foreign) {
        QueryBuilder joinedQuery = new QueryBuilder(clazz);
        join(joinedQuery, key, foreign, JoinType.FULL_JOIN);
        return joinedQuery;
    }

    public Query build() {
        StringBuilder sql = new StringBuilder();
        String groupBy = getGroupFields();
        String orderBy = getOrderedFields(groupBy);
        select(sql, groupBy);
        from(sql);
        where(sql);
        if (StringUtils.isNotBlank(groupBy)) {
            sql.append("group by ").append(groupBy);
            if (this.fields.contains(COUNT)) {
                sql.append(" ) r ");
            }
        }
        if (StringUtils.isNotBlank(orderBy) && !this.fields.contains(COUNT)) {
            sql.append("order by ").append(orderBy);
        }
        String sqlString = sql.toString()
                .replace(", ", Charactor.BLANK.value)
                .replace("where and", "where")
                .replace("where or", "where")
                .replace("(and", "(")
                .replace("(or", "(");
        Query query;
//        if (this.fields.contains(COUNT) || this.unknownEntity) {
//            query = this.entityManager.createNativeQuery(sqlString);
//        } else if (this.entity == null) {
//            query = this.entityManager.createNativeQuery(sqlString, this.table);
//        } else {
//            query = this.entityManager.createNativeQuery(sqlString, this.entity);
//        }
//        this.params.forEach(query::setParameter);
        return new Query();
    }

    private void select(StringBuilder sql, String groupBy) {
        if (this.fields.contains(COUNT)) {
            if (StringUtils.isNotBlank(groupBy)) {
                sql.append("select count(*) from ( select ").append(groupBy);
            }
            else {
                sql.append("select count(*)");
            }
            return;
        }
        StringBuilder query = new StringBuilder();
        if (this.joinMap != null || this.unknownEntity) {
            this.fields.forEach(field -> concatSelectQuery(query, this, field));
            if (!CollectionUtils.isEmpty(this.foreignFields)) {
                this.foreignFields.forEach(field -> query.append(field).append(Charactor.COMMA.value));
            }
        }
        else {
            for (Field field : this.table.getDeclaredFields()) {
                String fieldName = field.getName();
                if (this.fields.contains(fieldName)) {
                    concatSelectQuery(query, this, fieldName);
                } else {
                    fieldName = AppUtil.convertCamelToSnakeStyle(fieldName);
                    query.append("null as ").append(fieldName).append(Charactor.COMMA.value);
                }
            }
        }
        if (query.length() > 0) sql.append("select ").append(query);
        else sql.append("select *");
    }

    private void selectFromManyTables(StringBuilder sql) {
        this.fields.forEach(field -> concatSelectQuery(sql, this, field));
        if (!CollectionUtils.isEmpty(this.foreignFields)) {
            this.foreignFields.forEach(field -> sql.append(field).append(Charactor.COMMA.value));
        }
    }

    private void concatSelectQuery(StringBuilder sql, QueryBuilder query, String field) {
        if (StringUtils.isBlank(field)) return;
        String fieldName = AppUtil.convertCamelToSnakeStyle(field);
        if (!field.contains("concat(")) sql.append(query.tableName).append(Charactor.DOT.value);
        sql.append(fieldName);
        if (query.aliases != null && StringUtils.isNotBlank(query.aliases.get(fieldName))) {
            sql.append(" as ").append(query.aliases.get(fieldName));
        }
        sql.append(Charactor.COMMA.value);
    }

    private void from(StringBuilder sql) {
        sql.append(Charactor.BLANK.value)
                .append("from ").append(this.tableName)
                .append(Charactor.BLANK.value);
        join(this, sql);
    }

    private void join(QueryBuilder queryBuilder, StringBuilder sql) {
        if (queryBuilder.joinMap == null) return;
        queryBuilder.joinMap.forEach((key, query) -> {
            switch (query.getFirst()) {
                case INNER_JOIN: {
                    sql.append("inner join ");
                    break;
                }
                case LEFT_JOIN: {
                    sql.append("left join ");
                    break;
                }
                case RIGHT_JOIN: {
                    sql.append("right join ");
                    break;
                }
                case FULL_JOIN: {
                    sql.append("full outer join ");
                    break;
                }
            }
            sql.append(query.getSecond().tableName).append(" on ")
                    .append(query.getSecond().tableName).append(Charactor.DOT.value)
                    .append(AppUtil.convertCamelToSnakeStyle(key.getFirst()))
                    .append(" = ").append(queryBuilder.tableName).append(Charactor.DOT.value)
                    .append(AppUtil.convertCamelToSnakeStyle(key.getSecond()))
                    .append(Charactor.BLANK.value);
            join(query.getSecond(), sql);
        });
    }

    private void join(QueryBuilder joinedQuery, String key, String foreign, JoinType joinType) {
        if (this.joinMap == null) {
            this.joinMap = new HashMap<>();
        }
        this.joinMap.put(Pair.of(key, foreign), Pair.of(joinType, joinedQuery));
    }

    private void where(StringBuilder sql) {
        if (this.criteria == null) return;
        StringBuilder subQuery = new StringBuilder();
        subQuery.append(buildCriteria(this, this.criteria))
                .append(andOperation(this))
                .append(orOperation(this));
        if (this.joinMap != null) {
            this.joinMap.forEach((key, query) -> {
                subQuery.append(buildCriteria(query.getSecond(), query.getSecond().criteria))
                        .append(andOperation(query.getSecond()))
                        .append(orOperation(query.getSecond()));
            });
        }
        if (subQuery.length() > 0) sql.append("where ").append(subQuery);
    }

    private StringBuilder andOperation(QueryBuilder query) {
        if (query.criteria == null) return new StringBuilder();
        return operation(query, query.criteria.andOperations, Criteria.LogicalOperator.AND);
    }

    private StringBuilder orOperation(QueryBuilder query) {
        if (query.criteria == null) return new StringBuilder();
        return operation(query, query.criteria.orOperations, Criteria.LogicalOperator.OR);
    }

    private StringBuilder operation(QueryBuilder query, List<Criteria> operations, Criteria.LogicalOperator operator) {
        StringBuilder sql = new StringBuilder();
        if (CollectionUtils.isEmpty(operations)) return sql;
        for (Criteria operation : operations) {
            sql.append(getLogicalOperator(operator))
                    .append("(").append(buildCriteria(query, operation)).append(")")
                    .append(Charactor.BLANK.value);
        }
        return sql;
    }

    private StringBuilder buildCriteria(QueryBuilder query, Criteria criteria) {
        StringBuilder sql = new StringBuilder();
        if (criteria != null && !CollectionUtils.isEmpty(criteria.criteriaChain)) {
            criteria.criteriaChain.forEach(subCriteria -> {
                String param = this.PARAM + this.paramIndex;
                StringBuilder field = new StringBuilder();
                if (StringUtils.isNotBlank(subCriteria.alias)) {
                    if (this.aliases == null) return;
                    String alias = null;
                    for (Map.Entry<String, String> aliasEntry : this.aliases.entrySet()) {
                        if (aliasEntry.getValue().equals(subCriteria.alias)) {
                            alias = aliasEntry.getKey();
                            break;
                        }
                    }
                    field.append(AppUtil.convertCamelToSnakeStyle(alias));
                } else {
                    field.append(StringUtils.isNotBlank(subCriteria.tableName) ? subCriteria.tableName : query.tableName)
                            .append(Charactor.DOT.value).append(AppUtil.convertCamelToSnakeStyle(subCriteria.key));
                }
                if (!CollectionUtils.isEmpty(subCriteria.asDates) && subCriteria.asDates.contains(subCriteria.key)) {
                    sql.append(getLogicalOperator(subCriteria.logicalOperator))
                            .append("date(").append(field).append(")");
                } else {
                    sql.append(getLogicalOperator(subCriteria.logicalOperator)).append(field);
                }
                sql.append(getOperator(subCriteria.operator));
                if (!List.of(Criteria.Operator.IS_NULL, Criteria.Operator.IS_NOT_NULL).contains(subCriteria.operator)) {
                    sql.append(Charactor.COLON.value).append(param)
                            .append(Charactor.BLANK.value);
                    this.params.put(param, subCriteria.value);
                    this.paramIndex++;
                }
            });
        }
        return sql;
    }

    private String getLogicalOperator(Criteria.LogicalOperator logicalOperator) {
        if (logicalOperator == null) return Charactor.EMPTY.value;
        switch (logicalOperator) {
            case AND: return "and ";
            case OR: return "or ";
        }
        return "and ";
    }

    private String getGroupFields() {
        if (CollectionUtils.isEmpty(this.groupBy)) return Charactor.EMPTY.value;
        StringBuilder result = new StringBuilder();
        this.groupBy.forEach(item -> result.append(item.getFirst())
                .append(Charactor.DOT.value)
                .append(item.getSecond())
                .append(Charactor.COMMA.value));
        return result.append(Charactor.BLANK.value).toString();
    }

    private String getOrderedFields(String groupBy) {
        if (CollectionUtils.isEmpty(this.orderBy)) return null;
        StringBuilder result = new StringBuilder();
        this.orderBy.forEach(item -> {
            StringBuilder orderBy = new StringBuilder();
            if (item.getFirst().equals(Charactor.EMPTY.value)) {
                orderBy.append(item.getSecond());
            } else {
                orderBy.append(item.getFirst())
                        .append(Charactor.DOT.value)
                        .append(item.getSecond())
                        .append(Charactor.COMMA.value);
                String field = orderBy.toString()
                        .replace(Charactor.BLANK.value + "ASC", "")
                        .replace(Charactor.BLANK.value + "DESC", "");
                if (!groupBy.contains(field)) return;
            }
            result.append(orderBy);
        });
        return result.append(Charactor.BLANK.value).toString();
    }

    private String getOperator(Criteria.Operator operator) {
        if (operator == null) return Charactor.EMPTY.value;
        switch (operator) {
            case EQUAL: return " = ";
            case NOT_EQUAL: return " != ";
            case IN: return " in ";
            case LIKE: return " like ";
            case GREATER_THAN: return " > ";
            case GREATER_THAN_OR_EQUAL: return " >= ";
            case LESS_THAN: return " < ";
            case LESS_THAN_OR_EQUAL: return " <= ";
            case IS_NULL: return " is null ";
            case IS_NOT_NULL: return " is not null ";
        }
        return " = ";
    }

    private enum JoinType {
        INNER_JOIN,
        LEFT_JOIN,
        RIGHT_JOIN,
        FULL_JOIN
    }

    private enum Charactor {
        DOT("."),
        COMMA(","),
        COLON(":"),
        BLANK(" "),
        EMPTY("");

        private final String value;

        Charactor(String value) {
            this.value = value;
        }
    }
}
