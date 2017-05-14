package com.mito.exobj.BraceBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mito.exobj.MyLogger;

public class BB_BindHelper {

	public Map<Integer, List> bindedMap = new HashMap<Integer, List>();
	public Map<Integer, List> bindingMap = new HashMap<Integer, List>();
	public BB_DataWorld dataworld;

	public BB_BindHelper(BB_DataWorld world) {
		this.dataworld = world;
	}

	//本体のBraceのID、関連付けられるBraceのID、関連付けする場所のID
	public void register(int base, int id, int location) {
		ExtraObject base1 = this.dataworld.getBraceBaseByID(id);
		if (base1 != null) {
			this.register(base1, base, location);
		} else {
			//MyLogger.info("register " + id);
			if (bindedMap.containsKey(new Integer(id))) {
				bindedMap.get(new Integer(id)).add(new BB_BindLocation(base, location));
			} else {
				List list = new ArrayList();
				list.add(new BB_BindLocation(base, location));
				bindedMap.put(new Integer(id), list);
			}
		}
	}

	public void register(ExtraObject base, int id, int location) {
		if (bindingMap.containsKey(new Integer(id))) {
			bindingMap.get(new Integer(id)).add(new BB_BindLocation(base, location));
		} else {
			List list = new ArrayList();
			list.add(new BB_BindLocation(base, location));
			bindingMap.put(new Integer(id), list);
		}
	}

	public void call(ExtraObject base) {
		Integer id = new Integer(base.BBID);
		if (bindedMap.containsKey(id)) {
			List list = bindedMap.get(id);
			bindedMap.remove(id);
			for (int n = 0; n < list.size(); n++) {
				BB_BindLocation loc = (BB_BindLocation) list.get(n);
				if (loc != null) {
					ExtraObject base1 = base.dataworld.getBraceBaseByID(loc.id);
					if (base1 != null) {
						base1.addBrace(base, loc.location);
					} else {
						register(base, loc.id, loc.location);
					}
				}
			}
		}
		if (bindingMap.containsKey(id)) {
			List list = bindingMap.get(id);
			bindingMap.remove(id);
			for (int n = 0; n < list.size(); n++) {
				BB_BindLocation loc = (BB_BindLocation) list.get(n);
				if (loc != null) {
					base.addBrace(loc.base, loc.location);
				}
			}
		}
	}

	public void onUpdate() {

		MyLogger.info(" " + bindingMap.size() + " " + bindedMap.size());
	}

}
