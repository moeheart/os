// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: db.proto

package iiis.systems.os.blockchaindb;

public final class DBProto {
  private DBProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_blockdb_GetBlockRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_blockdb_GetBlockRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_blockdb_GetRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_blockdb_GetRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_blockdb_GetResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_blockdb_GetResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_blockdb_GetHeightResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_blockdb_GetHeightResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_blockdb_BooleanResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_blockdb_BooleanResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_blockdb_VerifyResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_blockdb_VerifyResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_blockdb_Null_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_blockdb_Null_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_blockdb_Transaction_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_blockdb_Transaction_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_blockdb_JsonBlockString_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_blockdb_JsonBlockString_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_blockdb_Block_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_blockdb_Block_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\010db.proto\022\007blockdb\"$\n\017GetBlockRequest\022\021" +
      "\n\tBlockHash\030\001 \001(\t\"\034\n\nGetRequest\022\016\n\006UserI" +
      "D\030\001 \001(\t\"\034\n\013GetResponse\022\r\n\005Value\030\001 \001(\005\"5\n" +
      "\021GetHeightResponse\022\016\n\006Height\030\001 \001(\005\022\020\n\010Le" +
      "afHash\030\002 \001(\t\"\"\n\017BooleanResponse\022\017\n\007Succe" +
      "ss\030\001 \001(\010\"\207\001\n\016VerifyResponse\022/\n\006Result\030\001 " +
      "\001(\0162\037.blockdb.VerifyResponse.Results\022\021\n\t" +
      "BlockHash\030\002 \001(\t\"1\n\007Results\022\n\n\006FAILED\020\000\022\013" +
      "\n\007PENDING\020\001\022\r\n\tSUCCEEDED\020\002\"\006\n\004Null\"\257\001\n\013T" +
      "ransaction\022(\n\004Type\030\001 \001(\0162\032.blockdb.Trans",
      "action.Types\022\016\n\006FromID\030\003 \001(\t\022\014\n\004ToID\030\004 \001" +
      "(\t\022\r\n\005Value\030\005 \001(\005\022\021\n\tMiningFee\030\006 \001(\005\022\014\n\004" +
      "UUID\030\007 \001(\t\"\"\n\005Types\022\013\n\007UNKNOWN\020\000\022\014\n\010TRAN" +
      "SFER\020\005J\004\010\002\020\003\"\037\n\017JsonBlockString\022\014\n\004Json\030" +
      "\001 \001(\t\"v\n\005Block\022\017\n\007BlockID\030\001 \001(\005\022\020\n\010PrevH" +
      "ash\030\002 \001(\t\022*\n\014Transactions\030\003 \003(\0132\024.blockd" +
      "b.Transaction\022\017\n\007MinerID\030\004 \001(\t\022\r\n\005Nonce\030" +
      "\005 \001(\t2\254\003\n\017BlockChainMiner\0222\n\003Get\022\023.block" +
      "db.GetRequest\032\024.blockdb.GetResponse\"\000\022<\n" +
      "\010Transfer\022\024.blockdb.Transaction\032\030.blockd",
      "b.BooleanResponse\"\000\0229\n\006Verify\022\024.blockdb." +
      "Transaction\032\027.blockdb.VerifyResponse\"\000\0228" +
      "\n\tGetHeight\022\r.blockdb.Null\032\032.blockdb.Get" +
      "HeightResponse\"\000\022@\n\010GetBlock\022\030.blockdb.G" +
      "etBlockRequest\032\030.blockdb.JsonBlockString" +
      "\"\000\0226\n\tPushBlock\022\030.blockdb.JsonBlockStrin" +
      "g\032\r.blockdb.Null\"\000\0228\n\017PushTransaction\022\024." +
      "blockdb.Transaction\032\r.blockdb.Null\"\000B)\n\034" +
      "iiis.systems.os.blockchaindbB\007DBProtoP\001b" +
      "\006proto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_blockdb_GetBlockRequest_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_blockdb_GetBlockRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_blockdb_GetBlockRequest_descriptor,
        new java.lang.String[] { "BlockHash", });
    internal_static_blockdb_GetRequest_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_blockdb_GetRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_blockdb_GetRequest_descriptor,
        new java.lang.String[] { "UserID", });
    internal_static_blockdb_GetResponse_descriptor =
      getDescriptor().getMessageTypes().get(2);
    internal_static_blockdb_GetResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_blockdb_GetResponse_descriptor,
        new java.lang.String[] { "Value", });
    internal_static_blockdb_GetHeightResponse_descriptor =
      getDescriptor().getMessageTypes().get(3);
    internal_static_blockdb_GetHeightResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_blockdb_GetHeightResponse_descriptor,
        new java.lang.String[] { "Height", "LeafHash", });
    internal_static_blockdb_BooleanResponse_descriptor =
      getDescriptor().getMessageTypes().get(4);
    internal_static_blockdb_BooleanResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_blockdb_BooleanResponse_descriptor,
        new java.lang.String[] { "Success", });
    internal_static_blockdb_VerifyResponse_descriptor =
      getDescriptor().getMessageTypes().get(5);
    internal_static_blockdb_VerifyResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_blockdb_VerifyResponse_descriptor,
        new java.lang.String[] { "Result", "BlockHash", });
    internal_static_blockdb_Null_descriptor =
      getDescriptor().getMessageTypes().get(6);
    internal_static_blockdb_Null_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_blockdb_Null_descriptor,
        new java.lang.String[] { });
    internal_static_blockdb_Transaction_descriptor =
      getDescriptor().getMessageTypes().get(7);
    internal_static_blockdb_Transaction_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_blockdb_Transaction_descriptor,
        new java.lang.String[] { "Type", "FromID", "ToID", "Value", "MiningFee", "UUID", });
    internal_static_blockdb_JsonBlockString_descriptor =
      getDescriptor().getMessageTypes().get(8);
    internal_static_blockdb_JsonBlockString_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_blockdb_JsonBlockString_descriptor,
        new java.lang.String[] { "Json", });
    internal_static_blockdb_Block_descriptor =
      getDescriptor().getMessageTypes().get(9);
    internal_static_blockdb_Block_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_blockdb_Block_descriptor,
        new java.lang.String[] { "BlockID", "PrevHash", "Transactions", "MinerID", "Nonce", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}