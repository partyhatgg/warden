package dev.partyhat.warden.fabric.mixin;

import dev.partyhat.warden.Warden;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
    @Inject(method = "shutdown", at = @At(value = "FIELD", target = "Lnet/minecraft/server/MinecraftServer;saving:Z", ordinal = 0))
    public void onShutdown(CallbackInfo ci) {
        Warden.getInstance().onShutdown();
//        Warden.getInstance()
    }
}
