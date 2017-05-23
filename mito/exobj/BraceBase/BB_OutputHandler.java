package com.mito.exobj.BraceBase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mito.exobj.client.render.model.BB_Model;
import com.mito.exobj.client.render.model.BB_ModelGroup;
import com.mito.exobj.client.render.model.BB_Polygon;
import com.mito.exobj.client.render.model.Vertex;

public class BB_OutputHandler {

	public static void outputObj(String name, File dir, BB_ModelGroup models) throws IOException {
		File file = new File(dir, name + ".obj");
		if (file.exists()) {
			return;
		}
		file.createNewFile();
		File file1 = new File(dir, name + ".mtl");
		if (file1.exists()) {
			return;
		}
		file1.createNewFile();

		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);

		List<String> headers = new ArrayList<String>();
		List<String> vertexs = new ArrayList<String>();
		List<String> vertexs_t = new ArrayList<String>();
		List<String> vertexs_n = new ArrayList<String>();
		List<String> group = new ArrayList<String>();

		headers.add("# This file made by Extra Objects for Minecraft 1.7.10.");
		headers.add("");
		headers.add("mtllib " + name + ".mtl");
		headers.add("g default");
		
		int i1 = 0;
		for (BB_Model model : models.models) {
			i1++;
			group.add("s off");
			group.add("g model" + i1);
			group.add("usemtl default");
			for (BB_Polygon p : model.planes) {
				int i = vertexs.size() - p.getLine().size();
				String poly = "f";
				for (Vertex v : p.getLine()) {
					i++;
					vertexs.add("v " + v.pos.xCoord + " " + v.pos.yCoord + " " + v.pos.zCoord);
					vertexs_t.add("vt " + v.u + " " + v.v);
					vertexs_n.add("vn " + v.norm.xCoord + " " + v.norm.yCoord + " " + v.norm.zCoord);
					poly = poly + " " + i + "/" + i + "/" + i;
				}
				group.add(poly);
			}
		}
		List<String> obj = new ArrayList<String>();
		obj.addAll(headers);
		obj.addAll(vertexs);
		obj.addAll(vertexs_t);
		obj.addAll(vertexs_n);
		obj.addAll(group);
		for (String s : obj) {
			bw.write(s + "\r\n", 0, s.length() + 1);
		}

		bw.close();
		
		List<String> mtl = new ArrayList<String>();
		mtl.add("newmtl default");
		mtl.add("illum 2");
		mtl.add("Kd 0.50 0.50 0.50");
		mtl.add("Ka 0.00 0.00 0.00");
		mtl.add("Tf 1.00 1.00 1.00");
		mtl.add("Ni 1.00");
		mtl.add("");
		FileWriter fw1 = new FileWriter(file1);
		BufferedWriter bw1 = new BufferedWriter(fw1);
		for (String s : mtl) {
			bw1.write(s + "\r\n", 0, s.length() + 1);
		}
		bw1.close();
	}

}
