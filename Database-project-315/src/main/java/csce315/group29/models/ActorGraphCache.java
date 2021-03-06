// automatically generated by the FlatBuffers compiler, do not modify

package csce315.group29.models;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class ActorGraphCache extends Table {
  public static ActorGraphCache getRootAsActorGraphCache(ByteBuffer _bb) { return getRootAsActorGraphCache(_bb, new ActorGraphCache()); }
  public static ActorGraphCache getRootAsActorGraphCache(ByteBuffer _bb, ActorGraphCache obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; vtable_start = bb_pos - bb.getInt(bb_pos); vtable_size = bb.getShort(vtable_start); }
  public ActorGraphCache __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public Actor data(int j) { return data(new Actor(), j); }
  public Actor data(Actor obj, int j) { int o = __offset(4); return o != 0 ? obj.__assign(__indirect(__vector(o) + j * 4), bb) : null; }
  public int dataLength() { int o = __offset(4); return o != 0 ? __vector_len(o) : 0; }

  public static int createActorGraphCache(FlatBufferBuilder builder,
      int dataOffset) {
    builder.startObject(1);
    ActorGraphCache.addData(builder, dataOffset);
    return ActorGraphCache.endActorGraphCache(builder);
  }

  public static void startActorGraphCache(FlatBufferBuilder builder) { builder.startObject(1); }
  public static void addData(FlatBufferBuilder builder, int dataOffset) { builder.addOffset(0, dataOffset, 0); }
  public static int createDataVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }
  public static void startDataVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static int endActorGraphCache(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
  public static void finishActorGraphCacheBuffer(FlatBufferBuilder builder, int offset) { builder.finish(offset); }
  public static void finishSizePrefixedActorGraphCacheBuffer(FlatBufferBuilder builder, int offset) { builder.finishSizePrefixed(offset); }
}

