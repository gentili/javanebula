package ca.mcpnet.javanebula.jme;

/*
 * Copyright (c) 2009-2010 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import static org.lwjgl.opengl.GL11.GL_BYTE;
import static org.lwjgl.opengl.GL11.GL_DOUBLE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_INT;
import static org.lwjgl.opengl.GL11.GL_SHORT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_SHORT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_STREAM_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glGenBuffers;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.opengl.NVHalfFloat;

/**
 * A <code>VertexBuffer</code> contains a particular type of geometry
 * data used by {@link Mesh}es. Every VertexBuffer set on a <code>Mesh</code>
 * is sent as an attribute to the vertex shader to be processed.
 */
public class VertexBuffer extends GLObject implements Cloneable {

    /**
     * Type of buffer. Specifies the actual attribute it defines.
     */
    public static enum Type {
        /**
         * Position of the vertex (3 floats)
         */
        Position,

        /**
         * The size of the point when using point buffers.
         */
        Size,

        /**
         * Normal vector, normalized.
         */
        Normal,

        /**
         * Texture coordinate
         */
        TexCoord,

        /**
         * Color and Alpha (4 floats)
         */
        Color,

        /**
         * Tangent vector, normalized.
         */
        Tangent,

        /**
         * Binormal vector, normalized.
         */
        Binormal,

        /**
         * Specifies the source data for various vertex buffers
         * when interleaving is used.
         */
        InterleavedData,

        /**
         * Do not use.
         */
        @Deprecated
        MiscAttrib,

        /**
         * Specifies the index buffer, must contain integer data.
         */
        Index,

        /** 
         * Inital vertex position, used with animation 
         */
        BindPosePosition,

        /** 
         * Inital vertex normals, used with animation
         */
        BindPoseNormal,

        /** 
         * Bone weights, used with animation
         */
        BoneWeight,

        /** 
         * Bone indices, used with animation
         */
        BoneIndex,

        /**
         * Texture coordinate #2
         */
        TexCoord2,

        /**
         * Texture coordinate #3
         */
        TexCoord3,

        /**
         * Texture coordinate #4
         */
        TexCoord4,

        /**
         * Texture coordinate #5
         */
        TexCoord5,

        /**
         * Texture coordinate #6
         */
        TexCoord6,

        /**
         * Texture coordinate #7
         */
        TexCoord7,

        /**
         * Texture coordinate #8
         */
        TexCoord8,
    }

    /**
     * The usage of the VertexBuffer, specifies how often the buffer
     * is used. This can determine if a vertex buffer is placed in VRAM
     * or held in video memory, but no guarantees are made- it's only a hint.
     */
    public static enum Usage {
        
        /**
         * Mesh data is sent once and very rarely updated.
         */
        Static,

        /**
         * Mesh data is updated occasionally (once per frame or less).
         */
        Dynamic,

        /**
         * Mesh data is updated every frame.
         */
        Stream,

        /**
         * Mesh data is not sent to GPU at all. It is only
         * used by the CPU.
         */
        CpuOnly;
    }

    public static enum Format {
        // Floating point formats
        Half(2),
        Float(4),
        Double(8),

        // Integer formats
        Byte(1),
        UnsignedByte(1),
        Short(2),
        UnsignedShort(2),
        Int(4),
        UnsignedInt(4);

        private int componentSize = 0;

        Format(int componentSize){
            this.componentSize = componentSize;
        }

        /**
         * @return Size in bytes of this data type.
         */
        public int getComponentSize(){
            return componentSize;
        }
    }

    protected int offset = 0;
    protected int lastLimit = 0;
    protected int stride = 0;
    protected int components = 0;

    /**
     * derived from components * format.getComponentSize()
     */
    protected transient int componentsLength = 0;
    protected Buffer data = null;
    protected Usage usage;
    protected Type bufType;
    protected Format format;
    protected boolean normalized = false;
    protected transient boolean dataSizeChanged = false;

    /**
     * Creates an empty, uninitialized buffer.
     * Must call setupData() to initialize.
     */
    public VertexBuffer(Type type){
        super(GLObject.Type.VertexBuffer);
        this.bufType = type;
    }

    /**
     * Do not use this constructor. Serialization purposes only.
     */
    public VertexBuffer(){
        super(GLObject.Type.VertexBuffer);
    }

    protected VertexBuffer(int id){
        super(GLObject.Type.VertexBuffer, id);
    }

    /**
     * @return The offset (in bytes) from the start of the buffer
     * after which the data is sent to the GPU.
     */
    public int getOffset() {
        return offset;
    }

    /**
     * @param offset Specify the offset (in bytes) from the start of the buffer
     * after which the data is sent to the GPU.
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * @return The stride (in bytes) for the data. If the data is packed
     * in the buffer, then stride is 0, if there's other data that is between
     * the current component and the next component in the buffer, then this
     * specifies the size in bytes of that additional data.
     */
    public int getStride() {
        return stride;
    }

    /**
     * @param stride The stride (in bytes) for the data. If the data is packed
     * in the buffer, then stride is 0, if there's other data that is between
     * the current component and the next component in the buffer, then this
     * specifies the size in bytes of that additional data.
     */
    public void setStride(int stride) {
        this.stride = stride;
    }

    /**
     * @return A native buffer, in the specified {@link Format format}.
     */
    public Buffer getData(){
        return data;
    }

    /**
     * @return The usage of this buffer. See {@link Usage} for more
     * information.
     */
    public Usage getUsage(){
        return usage;
    }

    /**
     * @param usage The usage of this buffer. See {@link Usage} for more
     * information.
     */
    public void setUsage(Usage usage){
//        if (id != -1)
//            throw new UnsupportedOperationException("Data has already been sent. Cannot set usage.");

        this.usage = usage;
    }

    /**
     * @param normalized Set to true if integer components should be converted
     * from their maximal range into the range 0.0 - 1.0 when converted to
     * a floating-point value for the shader.
     * E.g. if the {@link Format} is {@link Format#UnsignedInt}, then
     * the components will be converted to the range 0.0 - 1.0 by dividing
     * every integer by 2^32.
     */
    public void setNormalized(boolean normalized){
        this.normalized = normalized;
    }

    /**
     * @return True if integer components should be converted to the range 0-1.
     * @see VertexBuffer#setNormalized(boolean) 
     */
    public boolean isNormalized(){
        return normalized;
    }

    /**
     * @return The type of information that this buffer has.
     */
    public Type getBufferType(){
        return bufType;
    }

    /**
     * @return The {@link Format format}, or data type of the data.
     */
    public Format getFormat(){
        return format;
    }

    /**
     * @return The number of components of the given {@link Format format} per
     * element.
     */
    public int getNumComponents(){
        return components;
    }

    /**
     * @return The total number of data elements in the data buffer.
     */
    public int getNumElements(){
        int elements = data.capacity() / components;
        if (format == Format.Half)
            elements /= 2;
        return elements;
    }

    /**
     * Called to initialize the data in the <code>VertexBuffer</code>. Must only
     * be called once.
     * 
     * @param usage The usage for the data, or how often will the data
     * be updated per frame. See the {@link Usage} enum.
     * @param components The number of components per element.
     * @param format The {@link Format format}, or data-type of a single
     * component.
     * @param data A native buffer, the format of which matches the {@link Format}
     * argument.
     */
    public void setupData(Usage usage, int components, Format format, Buffer data){
        if (id != -1)
            throw new UnsupportedOperationException("Data has already been sent. Cannot setupData again.");

        if (usage == null || format == null || data == null)
            throw new IllegalArgumentException("None of the arguments can be null");
            
        if (components < 1 || components > 10)
            throw new IllegalArgumentException("components must be between 1 and 4");

        this.data = data;
        this.components = components;
        this.usage = usage;
        this.format = format;
        this.componentsLength = components * format.getComponentSize();
        this.lastLimit = data.limit();
        setUpdateNeeded();
    }

    /**
     * Called to update the data in the buffer with new data. Can only
     * be called after {@link VertexBuffer#setupData(com.jme3.scene.VertexBuffer.Usage, int, com.jme3.scene.VertexBuffer.Format, java.nio.Buffer) }
     * has been called. Note that it is fine to call this method on the
     * data already set, e.g. vb.updateData(vb.getData()), this will just
     * set the proper update flag indicating the data should be sent to the GPU
     * again.
     * It is allowed to specify a buffer with different capacity than the
     * originally set buffer.
     *
     * @param data The data buffer to set
     */
    public void updateData(Buffer data){
        if (id != -1){
            // request to update data is okay
        }

        // will force renderer to call glBufferData again
        if (data != null && (this.data.getClass() != data.getClass() || data.limit() != lastLimit)){
            dataSizeChanged = true;
            lastLimit = data.limit();
        }
        
        this.data = data;
        setUpdateNeeded();
    }

    public boolean hasDataSizeChanged() {
        return dataSizeChanged;
    }

    @Override
    public void clearUpdateNeeded(){
        super.clearUpdateNeeded();
        dataSizeChanged = false;
    }

    /**
     * Converts single floating-point data to {@link Format#Half half} floating-point data.
     */
    public void convertToHalf(){
        if (id != -1)
            throw new UnsupportedOperationException("Data has already been sent.");

        if (format != Format.Float)
            throw new IllegalStateException("Format must be float!");

        int numElements = data.capacity() / components;
        format = Format.Half;
        this.componentsLength = components * format.getComponentSize();
        
        ByteBuffer halfData = BufferUtils.createByteBuffer(componentsLength * numElements);
        halfData.rewind();

        FloatBuffer floatData = (FloatBuffer) data;
        floatData.rewind();

        for (int i = 0; i < floatData.capacity(); i++){
            float f = floatData.get(i);
            short half = FastMath.convertFloatToHalf(f);
            halfData.putShort(half);
        }
        this.data = halfData;
        setUpdateNeeded();
        dataSizeChanged = true;
    }

    /**
     * Reduces the capacity of the buffer to the given amount
     * of elements, any elements at the end of the buffer are truncated
     * as necessary.
     *
     * @param numElements
     */
    public void compact(int numElements){
        int total = components * numElements;
        data.clear();
        switch (format){
            case Byte:
            case UnsignedByte:
            case Half:
                ByteBuffer bbuf = (ByteBuffer) data;
                bbuf.limit(total);
                ByteBuffer bnewBuf = BufferUtils.createByteBuffer(total);
                bnewBuf.put(bbuf);
                data = bnewBuf;
                break;
            case Short:
            case UnsignedShort:
                ShortBuffer sbuf = (ShortBuffer) data;
                sbuf.limit(total);
                ShortBuffer snewBuf = BufferUtils.createShortBuffer(total);
                snewBuf.put(sbuf);
                data = snewBuf;
                break;
            case Int:
            case UnsignedInt:
                IntBuffer ibuf = (IntBuffer) data;
                ibuf.limit(total);
                IntBuffer inewBuf = BufferUtils.createIntBuffer(total);
                inewBuf.put(ibuf);
                data = inewBuf;
                break;
            case Float:
                FloatBuffer fbuf = (FloatBuffer) data;
                fbuf.limit(total);
                FloatBuffer fnewBuf = BufferUtils.createFloatBuffer(total);
                fnewBuf.put(fbuf);
                data = fnewBuf;
                break;
            default:
                throw new UnsupportedOperationException("Unrecognized buffer format: "+format);
        }
        data.clear();
        setUpdateNeeded();
        dataSizeChanged = true;
    }

    public void setElementComponent(int elementIndex, int componentIndex, Object val){
        int inPos = elementIndex * components;
        int elementPos = componentIndex;

        if (format == Format.Half){
            inPos *= 2;
            elementPos *= 2;
        }

        data.clear();

        switch (format){
            case Byte:
            case UnsignedByte:
            case Half:
                ByteBuffer bin = (ByteBuffer) data;
                bin.put(inPos + elementPos, (Byte)val);
                break;
            case Short:
            case UnsignedShort:
                ShortBuffer sin = (ShortBuffer) data;
                sin.put(inPos + elementPos, (Short)val);
                break;
            case Int:
            case UnsignedInt:
                IntBuffer iin = (IntBuffer) data;
                iin.put(inPos + elementPos, (Integer)val);
                break;
            case Float:
                FloatBuffer fin = (FloatBuffer) data;
                fin.put(inPos + elementPos, (Float)val);
                break;
            default:
                throw new UnsupportedOperationException("Unrecognized buffer format: "+format);
        }
    }

    public Object getElementComponent(int elementIndex, int componentIndex){
        int inPos = elementIndex * components;
        int elementPos = componentIndex;

        if (format == Format.Half){
            inPos *= 2;
            elementPos *= 2;
        }

        data.clear();

        switch (format){
            case Byte:
            case UnsignedByte:
            case Half:
                ByteBuffer bin = (ByteBuffer) data;
                return bin.get(inPos + elementPos);
            case Short:
            case UnsignedShort:
                ShortBuffer sin = (ShortBuffer) data;
                return sin.get(inPos + elementPos);
            case Int:
            case UnsignedInt:
                IntBuffer iin = (IntBuffer) data;
                return iin.get(inPos + elementPos);
            case Float:
                FloatBuffer fin = (FloatBuffer) data;
                return fin.get(inPos + elementPos);
            default:
                throw new UnsupportedOperationException("Unrecognized buffer format: "+format);
        }
    }

    /**
     * Copies a single element of data from this <code>VertexBuffer</code>
     * to the given output VertexBuffer.
     * 
     * @param inIndex
     * @param outVb
     * @param outIndex
     */
    public void copyElement(int inIndex, VertexBuffer outVb, int outIndex){
        if (outVb.format != format || outVb.components != components)
            throw new IllegalArgumentException("Buffer format mismatch. Cannot copy");

        int inPos  = inIndex  * components;
        int outPos = outIndex * components;
        int elementSz = components;
        if (format == Format.Half){
            // because half is stored as bytebuf but its 2 bytes long
            inPos *= 2;
            outPos *= 2;
            elementSz *= 2;
        }

        data.clear();
        outVb.data.clear();

        switch (format){
            case Byte:
            case UnsignedByte:
            case Half:
                ByteBuffer bin = (ByteBuffer) data;
                ByteBuffer bout = (ByteBuffer) outVb.data;
                bin.position(inPos).limit(inPos + elementSz);
                bout.position(outPos).limit(outPos + elementSz);
                bout.put(bin);
                break;
            case Short:
            case UnsignedShort:
                ShortBuffer sin = (ShortBuffer) data;
                ShortBuffer sout = (ShortBuffer) outVb.data;
                sin.position(inPos).limit(inPos + elementSz);
                sout.position(outPos).limit(outPos + elementSz);
                sout.put(sin);
                break;
            case Int:
            case UnsignedInt:
                IntBuffer iin = (IntBuffer) data;
                IntBuffer iout = (IntBuffer) outVb.data;
                iin.position(inPos).limit(inPos + elementSz);
                iout.position(outPos).limit(outPos + elementSz);
                iout.put(iin);
                break;
            case Float:
                FloatBuffer fin = (FloatBuffer) data;
                FloatBuffer fout = (FloatBuffer) outVb.data;
                fin.position(inPos).limit(inPos + elementSz);
                fout.position(outPos).limit(outPos + elementSz);
                fout.put(fin);
                break;
            default:
                throw new UnsupportedOperationException("Unrecognized buffer format: "+format);
        }

        data.clear();
        outVb.data.clear();
    }

    /**
     * Creates a {@link Buffer} that satisfies the given type and size requirements
     * of the parameters. The buffer will be of the type specified by
     * {@link Format format} and would be able to contain the given number
     * of elements with the given number of components in each element.
     *
     * @param format
     * @param components
     * @param numElements
     * @return
     */
    public static Buffer createBuffer(Format format, int components, int numElements){
        if (components < 1 || components > 4)
            throw new IllegalArgumentException("Num components must be between 1 and 4");

        int total = numElements * components;

        switch (format){
            case Byte:
            case UnsignedByte:
                return BufferUtils.createByteBuffer(total);
            case Half:
                return BufferUtils.createByteBuffer(total * 2);
            case Short:
            case UnsignedShort:
                return BufferUtils.createShortBuffer(total);
            case Int:
            case UnsignedInt:
                return BufferUtils.createIntBuffer(total);
            case Float:
                return BufferUtils.createFloatBuffer(total);
            case Double:
                return BufferUtils.createDoubleBuffer(total);
            default:
                throw new UnsupportedOperationException("Unrecoginized buffer format: "+format);
        }
    }

    @Override
    public VertexBuffer clone(){
        // NOTE: Superclass GLObject automatically creates shallow clone
        // e.g re-use ID.
        VertexBuffer vb = (VertexBuffer) super.clone();
        vb.handleRef = new Object();
        vb.id = -1;
        if (data != null)
            vb.updateData(BufferUtils.clone(data));
        
        return vb;
    }

    public VertexBuffer clone(Type overrideType){
        VertexBuffer vb = new VertexBuffer(overrideType);
        vb.components = components;
        vb.componentsLength = componentsLength;
        vb.data = BufferUtils.clone(data);
        vb.format = format;
        vb.handleRef = new Object();
        vb.id = -1;
        vb.normalized = normalized;
        vb.offset = offset;
        vb.stride = stride;
        vb.updateNeeded = true;
        vb.usage = usage;
        return vb;
    }

    @Override
    public String toString(){
        String dataTxt = null;
        if (data != null){
            dataTxt = ", elements="+data.capacity();
        }
        return getClass().getSimpleName() + "[fmt="+format.name()
                                            +", type="+bufType.name()
                                            +", usage="+usage.name()
                                            +dataTxt+"]";
    }

    @Override
    public void resetObject() {
//        assert this.id != -1;
        this.id = -1;
        setUpdateNeeded();
    }

//    @Override
//    public void deleteObject(Renderer r) {
//       r.deleteBuffer(this);
//    }

    @Override
    public GLObject createDestructableClone(){
        return new VertexBuffer(id);
    }
    
    /*********************************************************************\
    |* Vertex Buffers and Attributes                                     *|
    \*********************************************************************/
    private int convertUsage(Usage usage) {
        switch (usage) {
            case Static:
                return GL_STATIC_DRAW;
            case Dynamic:
                return GL_DYNAMIC_DRAW;
            case Stream:
                return GL_STREAM_DRAW;
            default:
                throw new UnsupportedOperationException("Unknown usage type.");
        }
    }

    @SuppressWarnings("unused")
	private int convertFormat(Format format) {
        switch (format) {
            case Byte:
                return GL_BYTE;
            case UnsignedByte:
                return GL_UNSIGNED_BYTE;
            case Short:
                return GL_SHORT;
            case UnsignedShort:
                return GL_UNSIGNED_SHORT;
            case Int:
                return GL_INT;
            case UnsignedInt:
                return GL_UNSIGNED_INT;
            case Half:
                return NVHalfFloat.GL_HALF_FLOAT_NV;
//                return ARBHalfFloatVertex.GL_HALF_FLOAT;
            case Float:
                return GL_FLOAT;
            case Double:
                return GL_DOUBLE;
            default:
                throw new UnsupportedOperationException("Unknown buffer format.");

        }
    }

    public void updateBufferDataOnGPU() {
        int bufId = this.getId();
        boolean created = false;
        if (bufId == -1) {
            // create buffer
            bufId = glGenBuffers();
            if (bufId <= 0) {
            	throw new RuntimeException("Unable to generate buffer ID");
            }
            this.setId(bufId);
            created = true;
        }

        // bind buffer
        int target;
        if (this.getBufferType() == VertexBuffer.Type.Index) {
            target = GL_ELEMENT_ARRAY_BUFFER;
            glBindBuffer(target, bufId);
        } else {
            target = GL_ARRAY_BUFFER;
            glBindBuffer(target, bufId);
        }

        int usage = convertUsage(this.getUsage());
        this.getData().rewind();

        if (created || this.hasDataSizeChanged()) {
            // upload data based on format
            switch (this.getFormat()) {
                case Byte:
                case UnsignedByte:
                    glBufferData(target, (ByteBuffer) this.getData(), usage);
                    break;
                //            case Half:
                case Short:
                case UnsignedShort:
                    glBufferData(target, (ShortBuffer) this.getData(), usage);
                    break;
                case Int:
                case UnsignedInt:
                    glBufferData(target, (IntBuffer) this.getData(), usage);
                    break;
                case Float:
                    glBufferData(target, (FloatBuffer) this.getData(), usage);
                    break;
                case Double:
                    glBufferData(target, (DoubleBuffer) this.getData(), usage);
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown buffer format.");
            }
        } else {
            switch (this.getFormat()) {
                case Byte:
                case UnsignedByte:
                    glBufferSubData(target, 0, (ByteBuffer) this.getData());
                    break;
                case Short:
                case UnsignedShort:
                    glBufferSubData(target, 0, (ShortBuffer) this.getData());
                    break;
                case Int:
                case UnsignedInt:
                    glBufferSubData(target, 0, (IntBuffer) this.getData());
                    break;
                case Float:
                    glBufferSubData(target, 0, (FloatBuffer) this.getData());
                    break;
                case Double:
                    glBufferSubData(target, 0, (DoubleBuffer) this.getData());
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown buffer format.");
            }
        }
        this.clearUpdateNeeded();
        // glBindBuffer(target,0);
    }
}
