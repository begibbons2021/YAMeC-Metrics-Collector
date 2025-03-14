// For reference when implementing the JNI and connecting the layers
// // Define a structure to hold our PDH resources
// struct PdhQueryData {
//     PDH_HQUERY hQuery;
//     PDH_HCOUNTER hCounter;
//          // Maintain separate counter handles for each metric which needs to be collected
//     bool isInitialized;
// };
//
// // Initialize and return a pointer cast to jlong for Java
// jlong initCpuCollection(JNIEnv* env, jobject obj) {
//     // Allocate our structure on the heap so it persists
//     auto* data = new PdhQueryData();
//     data->isInitialized = false;
//
//     // Open the query
//     if (PdhOpenQuery(nullptr, 0, &(data->hQuery)) != ERROR_SUCCESS) {
//         std::cerr << "Failed to open PDH query." << std::endl;
//         delete data;
//         return 0;
//     }
//
//     // Add the counter
//     if (PdhAddCounter(data->hQuery, TEXT("\\Processor(_Total)\\% Processor Time"), 0, &(data->hCounter)) != ERROR_SUCCESS) {
//         std::cerr << "Failed to add counter." << std::endl;
//         PdhCloseQuery(data->hQuery);
//         delete data;
//         return 0;
//     }
//
//     // Collect initial data for baseline
//     if (PdhCollectQueryData(data->hQuery) != ERROR_SUCCESS) {
//         std::cerr << "Failed to collect data." << std::endl;
//         PdhCloseQuery(data->hQuery);
//         delete data;
//         return 0;
//     }
//
//     data->isInitialized = true;
//     return reinterpret_cast<jlong>(data);
// }
//
// // Collect CPU usage using the handle provided by Java
// jdouble collectCpuUsage(JNIEnv* env, jobject obj, jlong handle) {
//     auto* data = reinterpret_cast<PdhQueryData*>(handle);
//     if (!data || !data->isInitialized) {
//         return -1.0;
//     }
//
//     PDH_FMT_COUNTERVALUE counterValue;
//
//     // Collect new data
//     if (PdhCollectQueryData(data->hQuery) != ERROR_SUCCESS) {
//         return -1.0;
//     }
//
//     // Get the formatted value
//     if (PdhGetFormattedCounterValue(data->hCounter, PDH_FMT_DOUBLE, nullptr, &counterValue) != ERROR_SUCCESS) {
//         return -1.0;
//     }
//
//     return static_cast<jdouble>(counterValue.doubleValue);
// }
//
// // Clean up resources
// jboolean closeCpuCollection(JNIEnv* env, jobject obj, jlong handle) {
//     PdhQueryData* data = reinterpret_cast<PdhQueryData*>(handle);
//     if (!data) {
//         return JNI_FALSE;
//     }
//
//     if (data->isInitialized) {
//         PdhCloseQuery(data->hQuery);
//     }
//
//     delete data;
//     return JNI_TRUE;
// }

// We'll need to make sure if our application fails, that close is killed.