package cz.cuni.mff.vkget.render;

import java.awt.Point;

import cz.cuni.mff.vkget.data.DataModel;
import cz.cuni.mff.vkget.data.RdfObject;
import cz.cuni.mff.vkget.layout.BlockLayout;
import cz.cuni.mff.vkget.layout.GveTable;
import cz.cuni.mff.vkget.layout.LineLayout;
import cz.cuni.mff.vkget.layout.ScreenLayout;

public class HtmlRenderer {
	
	public String render(DataModel dataModel, ScreenLayout screenLayout) {
		StringBuilder sb = new StringBuilder();
		
		for (GveTable table: dataModel.getTables()) {
			String type = table.getClassUri();
			renderTable(sb, table, screenLayout.findLayoutForType(type));
		}
		for (LineLayout layout: screenLayout.getLineLayouts()) {
			renderLineTitle(sb, layout);
		}
		sb.append("<script>");
		sb.append("var c = document.getElementById(\"panelCanvas\");");
		sb.append("var ctx = c.getContext(\"2d\");");
		for (LineLayout layout: screenLayout.getLineLayouts()) {
			renderLine(sb, layout);
		}
		sb.append("</script>");
		
		return sb.toString();
	}
	
	private void renderTable(StringBuilder sb, GveTable table, BlockLayout layout) {
		sb.append("<table cellspacing=\"0\"");
		
		sb.append(" style=\"");
		sb.append("position: absolute; table-layout: fixed; border-collapse: collapse;");
		sb.append("background-color: ").append(layout.getBackground()).append(";");
		sb.append("color: ").append(layout.getFontColor()).append(";");
		sb.append("font-size: ").append(layout.getFontSize()).append("px;");
		sb.append("left: ").append(layout.getLeft()).append("px;");
		sb.append("border: ").append(layout.getLineThickness()).append("px ")
			.append(layout.getLineType()).append(" ")
			.append(layout.getLineColor()).append(";");
		sb.append("top: ").append(layout.getTop()).append("px;");
		sb.append("width: ").append(layout.getWidth()).append("px;");
		sb.append("\"");
		
		sb.append(">");
		sb.append("<thead style=\"display: block;\"><tr><th class=\"caption\" colspan=\"").append(table.getColumnsURIs().size()).append("\">")
			.append(layout.getTitle()).append("</th></tr><tr>");
		for (String property: table.getColumnsURIs()) {
			sb.append("<th style=\"min-width: ").append(layout.getWidth()/table.getColumnsURIs().size()).append("px;\">");
			sb.append(property);
			sb.append("</th>");
		}
		sb.append("</tr></thead><tbody style=\"display: block; overflow-x: hidden; overflow-y: scroll;height: ").append(layout.getHeight()).append("px;\">");
		for (RdfObject instance: table.getInstances()) {
			sb.append("<tr>");
			for (String property: table.getColumnsURIs()) {
				sb.append("<td style=\"min-width: ").append(layout.getWidth()/table.getColumnsURIs().size()).append("px;\">");
				sb.append(instance.getValueForProperty(property));
				sb.append("</td>");
			}
			sb.append("</tr>");
		}
		
		sb.append("</tbody></table>");
	}
	
	private void renderLine(StringBuilder sb, LineLayout layout) {
		sb.append("ctx.beginPath();");
		if (layout.getPoints().size() > 1) {
			Point first = layout.getPoints().get(0);
			sb.append("ctx.moveTo(").append(first.x).append(", ").append(first.y).append(");");
			for (int i = 1; i < layout.getPoints().size(); i++) {
				Point point = layout.getPoints().get(i);
				sb.append("ctx.lineTo(").append(point.x).append(", ").append(point.y).append(");");
			}
		}
		sb.append("ctx.stroke();");
	}
	
	private void renderLineTitle(StringBuilder sb, LineLayout layout) {
		int middle = layout.getPoints().size() / 2 - 1;
		Point point1 = layout.getPoints().get(middle);
		Point point2 = layout.getPoints().get(middle + 1);
		sb.append("<div cellspacing=\"0\"");
		
		int left = (point1.x + point2.x) / 2;
		int top = (point1.y + point2.y) / 2;
		
		sb.append(" style=\"");
		sb.append("position: absolute;");
		sb.append("color: ").append(layout.getFontColor()).append(";");
		sb.append("font-size: ").append(layout.getFontSize()).append("px;");
		sb.append("left: ").append(left).append("px;");
		sb.append("top: ").append(top).append("px;");
		sb.append("\"");
		
		sb.append(">");	
		sb.append(layout.getTitle());
		sb.append("</div>");
	}

}
