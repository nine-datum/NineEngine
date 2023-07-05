package nine.geometry.collada;

import nine.buffer.Buffer;
import nine.buffer.MatrixBuffer;
import nine.buffer.TextValueBuffer;
import nine.math.Matrix4f;

public class ColladaBasicAnimationParser implements ColladaAnimationParser
{
    @Override
    public void read(ColladaNode node, ColladaAnimationReader reader)
    {
        node.children("COLLADA", root ->
        root.children("library_animations", lib ->
        lib.children("animation", animation ->
        animation.attribute("name", name ->
        {
            MutableBufferMapping<Float> buffers = new MutableBufferMapping<>();
            animation.children("source", source ->
            source.attribute("id", id ->
            source.children("float_array", array ->
            array.content(content ->
            {
                buffers.write("#" + id, new TextValueBuffer<>(content, Float::parseFloat));
            }))));

            animation.children("sampler", sampler ->
            sampler.children("input", input ->
            input.attribute("semantic", semantic ->
            input.attribute("source", source ->
            {
                buffers.write(semantic, buffers.map(source));
            }))));
            
            Buffer<Float> input = buffers.map("INPUT");
            Buffer<Float> output = buffers.map("OUTPUT");
            Buffer<Matrix4f> matrixBuffer = new MatrixBuffer(output);
            reader.read(name, new KeyFrameAnimation(input, matrixBuffer));
        }))));
    }   
}