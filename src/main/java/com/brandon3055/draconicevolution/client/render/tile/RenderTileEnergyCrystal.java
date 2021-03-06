package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCOBJParser;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.render.shader.ShaderProgram;
import codechicken.lib.render.state.GlStateManagerHelper;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.client.render.TESRBase;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.utils.Utils;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalBase;
import com.brandon3055.draconicevolution.blocks.energynet.tileentity.TileCrystalDirectIO;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.client.render.shaders.DEShaders;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import com.brandon3055.draconicevolution.utils.DETextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;

import java.util.Map;

/**
 * Created by brandon3055 on 6/11/2016.
 */
public class RenderTileEnergyCrystal extends TESRBase<TileCrystalBase> {
    private CCModel crystalFull;
    private CCModel crystalHalf;
    private CCModel crystalBase;

    public RenderTileEnergyCrystal() {
        Map<String, CCModel> map = CCOBJParser.parseObjModels(ResourceHelperDE.getResource("models/crystal.obj"));
        crystalFull = CCModel.combine(map.values());
        map = CCOBJParser.parseObjModels(ResourceHelperDE.getResource("models/crystal_half.obj"));
        crystalHalf = map.get("Crystal");
        crystalBase = map.get("Base");
    }

    @Override
    public void renderTileEntityAt(TileCrystalBase te, double x, double y, double z, float partialTicks, int destroyStage) {
        te.getFxHandler().renderCooldown = 5;
        GlStateManager.pushMatrix();
        GlStateManagerHelper.pushState();
        GlStateManager.disableLighting();
        setLighting(200);

        if (te instanceof TileCrystalDirectIO) {
            renderHalfCrystal((TileCrystalDirectIO) te, x, y, z, partialTicks, destroyStage, te.getTier());
        } else {
            renderCrystal(te, x, y, z, partialTicks, destroyStage, te.getTier());
        }

        resetLighting();
        GlStateManagerHelper.popState();
        GlStateManager.popMatrix();
    }

    public void renderCrystal(TileCrystalBase te, double x, double y, double z, float partialTicks, int destroyStage, int tier) {
        boolean trans = MinecraftForgeClient.getRenderPass() == 1;
        CCRenderState ccrs = CCRenderState.instance();
        Matrix4 mat = RenderUtils.getMatrix(new Vector3(x + 0.5, y + 0.5, z + 0.5), new Rotation(180 * MathHelper.torad, 1, 0, 0), -0.5);
        mat.apply(new Rotation((ClientEventHandler.elapsedTicks + partialTicks) / 400F, 0, 1, 0));

        if (destroyStage >= 0) {
            bindTexture(DESTROY_STAGES[destroyStage]);
            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
            crystalFull.render(ccrs, mat);
            ccrs.draw();
            return;
        }

        if (!trans) {
            //Render Crystal
            bindShader(te, x, y, z, partialTicks, tier);
            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
            crystalFull.render(ccrs, mat);
            ccrs.draw();
            releaseShader();
        }
        else if (!(DEShaders.useShaders() && DEConfig.useCrystalShaders)){
            //Render overlay if shaders are not supported
            ResourceHelperDE.bindTexture(DETextures.ENERGY_CRYSTAL_BASE);
            GlStateManager.enableBlend();
            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
            crystalFull.render(ccrs, mat);
            ccrs.draw();
        }
//
//        if (ClientEventHandler.playerHoldingWrench && te instanceof TileCrystalWirelessIO && !trans) {
//            TileCrystalWirelessIO tile = (TileCrystalWirelessIO) te;
//
//            Tessellator tess = Tessellator.getInstance();
//            VertexBuffer buffer = tess.getBuffer();
//            GlStateManager.pushMatrix();
//            GlStateManager.disableTexture2D();
//
//            GlStateManager.glLineWidth(4);
//            buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
//
//            for (BlockPos target : tile.getReceivers()) {
//
//                Vec3D offset = Vec3D.getCenter(te.getPos()).subtract(Vec3D.getCenter(target)).subtract(0.5, 0.5, 0.5);
//                buffer.pos(x + 0.5, y + 0.5, z + 0.5).color(0F, 1F, 0F, 1F).endVertex();
//                buffer.pos(x - offset.x, y - offset.y, z - offset.z).color(0F, 1F, 0F, 1F).endVertex();
//
//            }
//
//            tess.draw();
//
//            GlStateManager.enableTexture2D();
//            GlStateManager.popMatrix();
//        }
    }

    public void renderHalfCrystal(TileCrystalDirectIO te, double x, double y, double z, float partialTicks, int destroyStage, int tier) {
        boolean trans = MinecraftForgeClient.getRenderPass() == 1;
        ResourceHelperDE.bindTexture(DETextures.ENERGY_CRYSTAL_BASE);
        CCRenderState ccrs = CCRenderState.instance();
        Matrix4 mat = RenderUtils.getMatrix(new Vector3(x + 0.5, y + 1, z + 0.5), new Rotation(0, 0, 0, 0), -0.5);
        mat.apply(Rotation.sideOrientation(te.facing.value.getOpposite().getIndex(), 0).at(new Vector3(0, 1, 0)));

        if (destroyStage >= 0) {
            bindTexture(DESTROY_STAGES[destroyStage]);
            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
            crystalBase.render(ccrs, mat);
            mat.apply(new Rotation((ClientEventHandler.elapsedTicks + partialTicks) / 400F, 0, 1, 0));
            crystalHalf.render(ccrs, mat);
            ccrs.draw();
            return;
        }


        if (!trans) {
            //Render Base
            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
            crystalBase.render(ccrs, mat);
            ccrs.draw();

            //Apply Crystal Rotation
            mat.apply(new Rotation((ClientEventHandler.elapsedTicks + partialTicks) / 400F, 0, 1, 0));

            //Render Crystal
            ResourceHelperDE.bindTexture(DETextures.REACTOR_CORE);
            bindShader(te, x, y, z, partialTicks, tier);
            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
            crystalHalf.render(ccrs, mat);
            ccrs.draw();
            releaseShader();
        }
        else if (!(DEShaders.useShaders() && DEConfig.useCrystalShaders)) {
            //Render overlay if shaders are not supported
            GlStateManager.enableBlend();
            mat.apply(new Rotation((ClientEventHandler.elapsedTicks + partialTicks) / 400F, 0, 1, 0)).apply(new Scale(1.001));
            ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
            crystalHalf.render(ccrs, mat);
            ccrs.draw();
        }
    }

    private static float[] r = {0.0F, 0.55F, 1.0F};
    private static float[] g = {0.35F, 0.3F, 0.572F};
    private static float[] b = {0.65F, 0.9F, 0.172F};

    public void bindShader(TileCrystalBase te, double x, double y, double z, float partialTicks, int tier) {
        BlockPos pos = te == null ? new BlockPos(0, 0, 0) : te.getPos();
        double mm = MathHelper.clip((((x * x) + (y * y) + (z * z) - 5) / 512), 0, 1);
        if (DEShaders.useShaders() && DEConfig.useCrystalShaders && mm < 1) {
            DEShaders.eCrystalOp.setType(tier);
            DEShaders.eCrystalOp.setAnimation((ClientEventHandler.elapsedTicks + partialTicks) / 50);
            DEShaders.eCrystalOp.setMipmap((float) mm);
            DEShaders.energyCrystal.freeBindShader();

            float xrot = (float) Math.atan2(x + 0.5, z + 0.5);
            float dist = (float) Utils.getDistanceAtoB(Vec3D.getCenter(pos).x, Vec3D.getCenter(pos).z, Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posZ);
            float yrot = (float) net.minecraft.util.math.MathHelper.atan2(dist, y + 0.5);
            DEShaders.eCrystalOp.setAngle(xrot / -3.125F, yrot / 3.125F);
        }
        else {
            ResourceHelperDE.bindTexture(DETextures.ENERGY_CRYSTAL_NO_SHADER);
            GlStateManager.disableLighting();
            GlStateManager.color(r[tier], g[tier], b[tier], 0.5F);
        }
    }

    private void releaseShader() {
        if (DEShaders.useShaders() && DEConfig.useCrystalShaders) {
            ShaderProgram.unbindShader();
        }
    }
}
