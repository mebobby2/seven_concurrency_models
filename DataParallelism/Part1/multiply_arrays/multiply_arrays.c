#ifdef __APPLE__
#include <OpenCL/cl.h>
#else
#include <CL/cl.h>
#endif

#include <stdio.h>
#include <mach/mach_time.h>
#include <inttypes.h>

#define CHECK_STATUS(s)                                       \
  do                                                          \
  {                                                           \
    cl_int ss = (s);                                          \
    if (ss != CL_SUCCESS)                                     \
    {                                                         \
      fprintf(stderr, "Error %d at line %d\n", ss, __LINE__); \
      exit(1);                                                \
    }                                                         \
  } while (0)

#define NUM_ELEMENTS (1024 * 100)

char *read_source(const char *filename)
{
  FILE *h = fopen(filename, "r");

  if (!h)
  {
    fprintf(stderr, "Unable to open file %s", filename);
  }

  fseek(h, 0, SEEK_END);
  size_t s = ftell(h);
  rewind(h);
  char *program = (char *)malloc(s + 1);
  fread(program, sizeof(char), s, h);
  program[s] = '\0';
  fclose(h);
  return program;
}

void random_fill(cl_float array[], size_t size)
{
  for (int i = 0; i < size; ++i)
    array[i] = (cl_float)rand() / RAND_MAX;
}

int main()
{
  cl_int status;

  cl_platform_id platform;
  CHECK_STATUS(clGetPlatformIDs(1, &platform, NULL));

  cl_device_id device;
  CHECK_STATUS(clGetDeviceIDs(platform, CL_DEVICE_TYPE_GPU, 1, &device, NULL));

  cl_context context = clCreateContext(NULL, 1, &device, NULL, NULL, &status);
  CHECK_STATUS(status);

  cl_command_queue queue = clCreateCommandQueue(context, device, CL_QUEUE_PROFILING_ENABLE, &status);
  CHECK_STATUS(status);

  char *source = read_source("multiply_arrays.cl");
  cl_program program = clCreateProgramWithSource(context, 1,
                                                 (const char **)&source, NULL, &status);
  CHECK_STATUS(status);
  free(source);

  CHECK_STATUS(clBuildProgram(program, 0, NULL, NULL, NULL, NULL));

  cl_kernel kernel = clCreateKernel(program, "multiply_arrays", &status);
  CHECK_STATUS(status);

  cl_float a[NUM_ELEMENTS], b[NUM_ELEMENTS];
  random_fill(a, NUM_ELEMENTS);
  random_fill(b, NUM_ELEMENTS);

  uint64_t startGPU = mach_absolute_time();

  cl_mem inputA = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                 sizeof(cl_float) * NUM_ELEMENTS, a, &status);
  CHECK_STATUS(status);
  cl_mem inputB = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                 sizeof(cl_float) * NUM_ELEMENTS, b, &status);
  CHECK_STATUS(status);
  cl_mem output = clCreateBuffer(context, CL_MEM_WRITE_ONLY,
                                 sizeof(cl_float) * NUM_ELEMENTS, NULL, &status);
  CHECK_STATUS(status);

  clSetKernelArg(kernel, 0, sizeof(cl_mem), &inputA);
  clSetKernelArg(kernel, 1, sizeof(cl_mem), &inputB);
  clSetKernelArg(kernel, 2, sizeof(cl_mem), &output);

  cl_event timing_event;
  size_t work_units = NUM_ELEMENTS;
  CHECK_STATUS(clEnqueueNDRangeKernel(queue, kernel, 1, NULL, &work_units,
                         NULL, 0, NULL, &timing_event));

  cl_float results[NUM_ELEMENTS];
  CHECK_STATUS(clEnqueueReadBuffer(queue, output, CL_TRUE, 0, sizeof(cl_float) * NUM_ELEMENTS,
                      results, 0, NULL, NULL));
  uint64_t endGPU = mach_absolute_time();

  for (int i = 0; i < NUM_ELEMENTS; ++i)
  {
    printf("%f * %f = %f\n", a[i], b[i], results[i]);
  }

  printf("\n");

  printf("Total (GPU): %lu ns\n\n", (unsigned long)(endGPU - startGPU));

  cl_ulong starttime;
  CHECK_STATUS(clGetEventProfilingInfo(timing_event, CL_PROFILING_COMMAND_START,
                          sizeof(cl_ulong), &starttime, NULL));
  cl_ulong endtime;
  CHECK_STATUS(clGetEventProfilingInfo(timing_event, CL_PROFILING_COMMAND_END,
                          sizeof(cl_ulong), &endtime, NULL));

  printf("Elapsed (GPU): %lu ns\n\n", (unsigned long)(endtime - starttime));
  CHECK_STATUS(clReleaseEvent(timing_event));
  CHECK_STATUS(clReleaseMemObject(inputA));
  CHECK_STATUS(clReleaseMemObject(inputB));
  CHECK_STATUS(clReleaseMemObject(output));
  CHECK_STATUS(clReleaseKernel(kernel));
  CHECK_STATUS(clReleaseProgram(program));
  CHECK_STATUS(clReleaseCommandQueue(queue));
  CHECK_STATUS(clReleaseContext(context));

  uint64_t startCPU = mach_absolute_time();

  for (int i = 0; i < NUM_ELEMENTS; ++i)
    results[i] = a[i] * b[i];

  uint64_t endCPU = mach_absolute_time();
  printf("Elapsed (CPU): %lu ns\n\n", (unsigned long)(endCPU - startCPU));

  return 0;
}
