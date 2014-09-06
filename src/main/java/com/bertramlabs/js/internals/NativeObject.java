package com.bertramlabs.js.internals;

import com.bertramlabs.js.internals.interfaces.JSObject;
import gnu.trove.map.hash.TLongObjectHashMap;

public abstract class NativeObject implements JSObject {
	// TODO: Implement prototype Structure, delegation, maps, etc
	TLongObjectHashMap properties; //Property Map
}
