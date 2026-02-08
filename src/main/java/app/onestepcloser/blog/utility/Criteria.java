package app.onestepcloser.blog.utility;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Criteria {
    protected String tableName;
    protected String key;
    protected String alias;
    protected Object value;
    protected Operator operator;
    protected LogicalOperator logicalOperator;
    protected List<Criteria> criteriaChain;
    protected List<Criteria> andOperations;
    protected List<Criteria> orOperations;
    protected Set<String> asDates;

    public Criteria() {
        this.criteriaChain = new ArrayList<>();
    }

    private Criteria(Operator operator) {
        this.operator = operator;
    }

    public Criteria and(String key) {
        this.key = key;
        this.logicalOperator = LogicalOperator.AND;
        return this;
    }

    public Criteria or(String key) {
        this.key = key;
        this.logicalOperator = LogicalOperator.OR;
        return this;
    }

    public Criteria and(Class<?> clazz, String key) {
        this.tableName = AppUtil.convertCamelToSnakeStyle(clazz.getSimpleName());
        this.key = key;
        this.logicalOperator = LogicalOperator.AND;
        return this;
    }

    public Criteria or(Class<?> clazz, String key) {
        this.tableName = AppUtil.convertCamelToSnakeStyle(clazz.getSimpleName());
        this.key = key;
        this.logicalOperator = LogicalOperator.OR;
        return this;
    }

    public Criteria andAlias(String alias) {
        this.alias = alias;
        this.logicalOperator = LogicalOperator.AND;
        return this;
    }

    public Criteria orAlias(String alias) {
        this.alias = alias;
        this.logicalOperator = LogicalOperator.OR;
        return this;
    }

    public Criteria equal(Object value) {
        this.criteriaChain.add(clone(value, Operator.EQUAL));
        this.tableName = null;
        return this;
    }

    public Criteria notEqual(Object value) {
        this.criteriaChain.add(clone(value, Operator.NOT_EQUAL));
        this.tableName = null;
        return this;
    }

    public Criteria in(Object value) {
        this.criteriaChain.add(clone(value, Operator.IN));
        this.tableName = null;
        return this;
    }

    public Criteria like(Object value) {
        this.criteriaChain.add(clone(value, Operator.LIKE));
        this.tableName = null;
        return this;
    }

    public Criteria gt(Object value) {
        this.criteriaChain.add(clone(value, Operator.GREATER_THAN));
        this.tableName = null;
        return this;
    }

    public Criteria gte(Object value) {
        this.criteriaChain.add(clone(value, Operator.GREATER_THAN_OR_EQUAL));
        this.tableName = null;
        return this;
    }

    public Criteria lt(Object value) {
        this.criteriaChain.add(clone(value, Operator.LESS_THAN));
        this.tableName = null;
        return this;
    }

    public Criteria lte(Object value) {
        this.criteriaChain.add(clone(value, Operator.LESS_THAN_OR_EQUAL));
        this.tableName = null;
        return this;
    }

    public Criteria isNull() {
        this.criteriaChain.add(clone(null, Operator.IS_NULL));
        this.tableName = null;
        return this;
    }

    public Criteria isNotNull() {
        this.criteriaChain.add(clone(null, Operator.IS_NOT_NULL));
        this.tableName = null;
        return this;
    }

    public Criteria andOperation(Criteria criteria) {
        if (CollectionUtils.isEmpty(this.andOperations)) {
            this.andOperations = new ArrayList<>();
        }
        this.andOperations.add(criteria);
        return this;
    }

    public Criteria orOperation(Criteria criteria) {
        if (CollectionUtils.isEmpty(this.orOperations)) {
            this.orOperations = new ArrayList<>();
        }
        this.orOperations.add(criteria);
        return this;
    }

    public Criteria asDate() {
        if (CollectionUtils.isEmpty(this.asDates)) {
            this.asDates = new HashSet<>();
        }
        this.asDates.add(this.key);
        return this;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setLogicalOperator(LogicalOperator logicalOperator) {
        this.logicalOperator = logicalOperator;
    }

    public void setAsDates(Set<String> asDates) {
        this.asDates = asDates;
    }

    public enum Operator {
        EQUAL,
        NOT_EQUAL,
        IN,
        LIKE,
        GREATER_THAN,
        GREATER_THAN_OR_EQUAL,
        LESS_THAN,
        LESS_THAN_OR_EQUAL,
        IS_NULL,
        IS_NOT_NULL,
    }

    public enum LogicalOperator {
        AND,
        OR
    }

    private Criteria clone(Object value, Operator operator) {
        Criteria criteria = new Criteria(operator);
        criteria.setTableName(this.tableName);
        criteria.setKey(this.key);
        criteria.setAlias(this.alias);
        criteria.setLogicalOperator(this.logicalOperator);
        criteria.setAsDates(this.asDates);
        criteria.setValue(value);
        return criteria;
    }
}
