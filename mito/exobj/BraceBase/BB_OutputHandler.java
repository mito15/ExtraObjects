package com.mito.exobj.BraceBase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mito.exobj.client.render.model.BB_Model;

public class BB_OutputHandler {

	public static void outputObj(String name, File dir, BB_Model model) throws IOException {
		File file = new File(dir, name);
		file.exists();
		file.createNewFile();

		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);

		List<String> vertexs = new ArrayList<String>();
		List<String> vertexs_n = new ArrayList<String>();
		List<String> vertexs_t = new ArrayList<String>();
		List<String> polygon = new ArrayList<String>();
		while ((rec = br.readLine()) != null) {
			bw.write(rec + "\r\n", 0, rec.length() + 1);
		}
		bw.close();
	}

}
