add_subdirectory(csrc)

if(SHERPA_ONNX_ENABLE_JNI)
  add_subdirectory(jni)
endif()

