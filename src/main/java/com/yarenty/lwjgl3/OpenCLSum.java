package com.yarenty.lwjgl3;

import org.lwjgl.opencl.CLUtil;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import org.lwjgl.opencl.CL;
import org.lwjgl.opencl.CLCreateContextCallback;
import org.lwjgl.opencl.CLDevice;
import org.lwjgl.opencl.CLPlatform;
import org.lwjgl.opencl.CLProgramCallback;

import static org.lwjgl.opencl.CL10.*;
import static org.lwjgl.opencl.CLUtil.checkCLError;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memDecodeUTF8;
 
public class OpenCLSum {
    // The OpenCL kernel
    static final String source =
        "kernel void sum(global const float *a, global const float *b, global float *answer) { "
        + "  unsigned int xid = get_global_id(0); "
        + "  answer[xid] = a[xid] + b[xid];"
        + "}";
 
    // Data buffers to store the input and result data in
    static final FloatBuffer a = toFloatBuffer(new float[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
    static final FloatBuffer b = toFloatBuffer(new float[]{9, 8, 7, 6, 5, 4, 3, 2, 1, 0});
    static final FloatBuffer answer = BufferUtils.createFloatBuffer(a.capacity());
 
    
	private static final CLCreateContextCallback CREATE_CONTEXT_CALLBACK = new CLCreateContextCallback() {
		@Override
		public void invoke(long errinfo, long private_info, long cb, long user_data) {
			System.err.println("[LWJGL] cl_create_context_callback");
			System.err.println("\tInfo: " + memDecodeUTF8(errinfo));
		}
	};
    
    public static void main(String[] args) throws Exception {
    	

    	
        // Initialize OpenCL and create a context and command queue
        CL.create();
        
        
        CLPlatform platform = CLPlatform.getPlatforms().get(0);

        PointerBuffer ctxProps = BufferUtils.createPointerBuffer(3);
		ctxProps.put(CL_CONTEXT_PLATFORM).put(platform).put(0).flip();
		
		
        IntBuffer errcode_ret = BufferUtils.createIntBuffer(1);
        
        List<CLDevice> devices = platform.getDevices(CL_DEVICE_TYPE_GPU);
       // long context = clCreateContext(platform, devices, null, null, null);
		long context = clCreateContext(ctxProps, devices.get(0).getPointer(), CREATE_CONTEXT_CALLBACK, NULL, errcode_ret);
		checkCLError(errcode_ret);
        //CLCommandQueue queue = clCreateCommandQueue(context, devices.get(0), CL_QUEUE_PROFILING_ENABLE, null);
        long queue = clCreateCommandQueue(context, devices.get(0).getPointer(), CL_QUEUE_PROFILING_ENABLE, errcode_ret);
 
        // Allocate memory for our two input buffers and our result buffer
        long aMem = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, a, null);
        //long buffer = clCreateBuffer(context, CL_MEM_READ_ONLY, 128, errcode_ret);
        clEnqueueWriteBuffer(queue, aMem, 1, 0, a, null, null);
        long bMem = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, b, null);
        clEnqueueWriteBuffer(queue, bMem, 1, 0, b, null, null);
        long answerMem = clCreateBuffer(context, CL_MEM_WRITE_ONLY | CL_MEM_COPY_HOST_PTR, answer, null);
        clFinish(queue);
 
        // Create our program and kernel
        long program = clCreateProgramWithSource(context, source, null);
        
    	//public static int clBuildProgram(long program, long device, CharSequence options, CLProgramCallback pfn_notify, long user_data) {

    		
        CLUtil.checkCLError(clBuildProgram(program, devices.get(0).getPointer(), "", null, 0L));
        // sum has to match a kernel method name in the OpenCL source
        long kernel = clCreateKernel(program, "sum", null);
 
        // Execution our kernel
        PointerBuffer kernel1DGlobalWorkSize = BufferUtils.createPointerBuffer(1);
        kernel1DGlobalWorkSize.put(0, a.capacity());
        clSetKernelArg(kernel, 0, aMem);
        clSetKernelArg(kernel, 1, bMem);
        clSetKernelArg(kernel, 2, answerMem);
        clEnqueueNDRangeKernel(queue, kernel, 1, null, kernel1DGlobalWorkSize, null, null, null);
 
        // Read the results memory back into our result buffer
        clEnqueueReadBuffer(queue, answerMem, 1, 0, answer, null, null);
        clFinish(queue);
        // Print the result memory
        print(a);
        System.out.println("+");
        print(b);
        System.out.println("=");
        print(answer);
 
        // Clean up OpenCL resources
        clReleaseKernel(kernel);
        clReleaseProgram(program);
        clReleaseMemObject(aMem);
        clReleaseMemObject(bMem);
        clReleaseMemObject(answerMem);
        clReleaseCommandQueue(queue);
        clReleaseContext(context);
        CL.destroy();
    }
 
 
    /** Utility method to convert float array to float buffer
     * @param floats - the float array to convert
     * @return a float buffer containing the input float array
     */
    static FloatBuffer toFloatBuffer(float[] floats) {
        FloatBuffer buf = BufferUtils.createFloatBuffer(floats.length).put(floats);
        buf.rewind();
        return buf;
    }
 
 
    /** Utility method to print a float buffer
     * @param buffer - the float buffer to print to System.out
     */
    static void print(FloatBuffer buffer) {
        for (int i = 0; i < buffer.capacity(); i++) {
            System.out.print(buffer.get(i)+" ");
        }
        System.out.println("");
    }
 
}