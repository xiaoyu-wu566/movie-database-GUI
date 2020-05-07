// automatically generated by the FlatBuffers compiler, do not modify

package csce315.group29.models;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class ActorConnection extends Table {
  public static ActorConnection getRootAsActorConnection(ByteBuffer _bb) { return getRootAsActorConnection(_bb, new ActorConnection()); }
  public static ActorConnection getRootAsActorConnection(ByteBuffer _bb, ActorConnection obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; vtable_start = bb_pos - bb.getInt(bb_pos); vtable_size = bb.getShort(vtable_start); }
  public ActorConnection __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public String nconst() { int o = __offset(4); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer nconstAsByteBuffer() { return __vector_as_bytebuffer(4, 1); }
  public ByteBuffer nconstInByteBuffer(ByteBuffer _bb) { return __vector_in_bytebuffer(_bb, 4, 1); }
  public String tconst() { int o = __offset(6); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer tconstAsByteBuffer() { return __vector_as_bytebuffer(6, 1); }
  public ByteBuffer tconstInByteBuffer(ByteBuffer _bb) { return __vector_in_bytebuffer(_bb, 6, 1); }

  public static int createActorConnection(FlatBufferBuilder builder,
      int nconstOffset,
      int tconstOffset) {
    builder.startObject(2);
    ActorConnection.addTconst(builder, tconstOffset);
    ActorConnection.addNconst(builder, nconstOffset);
    return ActorConnection.endActorConnection(builder);
  }

  public static void startActorConnection(FlatBufferBuilder builder) { builder.startObject(2); }
  public static void addNconst(FlatBufferBuilder builder, int nconstOffset) { builder.addOffset(0, nconstOffset, 0); }
  public static void addTconst(FlatBufferBuilder builder, int tconstOffset) { builder.addOffset(1, tconstOffset, 0); }
  public static int endActorConnection(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
}
