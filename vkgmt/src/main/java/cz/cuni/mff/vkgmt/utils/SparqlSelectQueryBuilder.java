package cz.cuni.mff.vkgmt.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class SparqlSelectQueryBuilder {
	
	private Map<String, String> namespaces = new HashMap<>();
	private boolean selectDistinct;
	private boolean selectAll;
	private List<String> selects;
	private List<WhereClause> wheres = new ArrayList<>();
	private ValueClause values;
	private List<String> filters = new ArrayList<>();
	private String graph;
	private String orderBy;
	private int limit;
	private int offset;
	
	private class ValueClause {
		private String var;
		private List<String> values = new ArrayList<>();
		
		public ValueClause(String var, List<String> values) {
			this.var = var;
			this.values.addAll(values);
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("VALUES ");
			sb.append(var);
			sb.append(" {\n");
			for (String value: values) {
				sb.append(" ").append(value);
			}
			sb.append("\n}\n");
			return sb.toString();
		}
	}
	
	private class WhereClause {
		private String subject;
		private String predicate;
		private String objekt;
		private boolean optional;
		
		public WhereClause(String s, String p, String o) {
			this.subject = s;
			this.predicate = p;
			this.objekt = o;
		}
		public WhereClause(String s, String p, String o, boolean optional) {
			this.subject = s;
			this.predicate = p;
			this.objekt = o;
			this.optional = optional;
		}
		
		@Override
		public String toString() {
			return (optional ? "OPTIONAL { " : "") + subject + " " + predicate + " " + objekt + " ." + (optional ? " }" : "");
		}
	}
	
	public void addWhereValue(String var, List<String> values) {
		this.values = new ValueClause(var, values);
	}
	
	public void setGraph(String graph) {
		this.graph = graph;
	}

	public void addFilter(String filter) {
		this.filters.add(filter);
	}
	
	public void addWhere(String s, String p, String o) {
		wheres.add(new WhereClause(s, p, o));
	}
	
	public void addWhereOptional(String s, String p, String o) {
		wheres.add(new WhereClause(s, p, o, true));
	}
	
	public void addSelect(String select) {
		if ("*".equals(select)) {
			throw new RuntimeException("Don't add '*' as new select, use selectAll method instead.");
		}
		if (selectAll) {
			selectAll = false;
			selects = new ArrayList<String>();
		}
		if (selects == null) {
			selects = new ArrayList<String>();
		}
		selects.add(select);
	}
	
	public void addWhere() {
		
	}
	
	public void selectAll() {
		this.selectAll = true;
		selects = new ArrayList<String>();
		selects.add("*");
	}
	
	public void setSelectDistinct(boolean selectDistinct) {
		this.selectDistinct = selectDistinct;
	}


	public void addNamespace(String prefix, String namespace) {
		namespaces.put(prefix, namespace);
	}
	
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	
	public void setLimit(int limit) {
		this.limit = limit;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String prefix: namespaces.keySet()) {
			String namespace = namespaces.get(prefix);
			sb.append("PREFIX ").append(prefix).append(": <").append(namespace).append(">\n");
		}
		sb.append("\n");
		sb.append("SELECT");
		if (this.selectDistinct) {
			sb.append(" DISTINCT");
		}
		if (this.selectAll) {
			sb.append(" *");
		} else {
			for (String select: selects) {
				sb.append(" ?" + select);
			}
			sb.append("\n");
		}
		
		if (StringUtils.isNotEmpty(graph)) {
			sb.append(" FROM NAMED <").append(graph).append("> WHERE {{ GRAPH <").append(graph).append("> {\n");
		} else {
			sb.append(" WHERE {{\n");
		}
		
		for (WhereClause whereClause: wheres) {
			sb.append("\t").append(whereClause.toString()).append("\n");
		}
		
		if (values != null) {
			values.toString();
		}
		
		sb.append("}\n");
		if (filters.size() > 0) {
			sb.append("FILTER (\n");
			boolean first = true;
			for (String filter: filters) {
				if (!first) {
					sb.append(" && ");
				} else {
					first = false;
				}
				sb.append(filter);
			}
			sb.append(")\n");
		}
		if (StringUtils.isNotEmpty(graph)) {
			sb.append(" }\n");
		}
		sb.append("\n}");
		if (StringUtils.isNotEmpty(orderBy)) {
			sb.append("ORDER BY ?uri\n");
		}
		if (limit > 0) {
			sb.append("LIMIT ").append(limit).append("\n");
		}
		if (offset > 0) {
			sb.append("OFFSET ").append(offset).append("\n");
		}
		String query = sb.toString();
		return query;
	}

}
