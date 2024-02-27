package dev.partyhat.warden.fabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import dev.partyhat.warden.JoinHandler;
import dev.partyhat.warden.Warden;
import dev.partyhat.warden.util.player.Player;
import net.kyori.adventure.platform.fabric.FabricServerAudiences;
import net.kyori.adventure.text.Component;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerManager.class)
public class MixinPlayerManager {

    @Shadow @Final private MinecraftServer server;

    @WrapOperation(method = "checkCanJoin", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;isWhitelisted(Lcom/mojang/authlib/GameProfile;)Z"))
    public boolean checkMigratoryPeriod(PlayerManager instance, GameProfile profile, Operation<Boolean> original) {
        System.out.println("check migration");
        return original.call(instance, profile) && !Warden.isMigratoryPeriod;
    }

    @Inject(method = "checkCanJoin", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/BannedIpList;isBanned(Ljava/net/SocketAddress;)Z"), cancellable = true)
    public void handleLogin(CallbackInfoReturnable<Text> cir, @Local GameProfile gameProfile) {
        System.out.println("login");
        if (Warden.isMigratoryPeriod) return;
        JoinHandler.LoginResult result = Warden.getInstance().getJoinHandler().handleLogin(new Player(gameProfile.getId(), gameProfile.getName()));
        if (!result.shouldAllow()) {
            cir.setReturnValue(FabricServerAudiences.of(server).toNative(result.reason()));
        } else {
            cir.setReturnValue(null);
        }
    }

    @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    public void handleJoin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        System.out.println("join");
        Component component = Warden.getInstance().getJoinHandler().handleJoin(new Player(player.getUuid(), player.getName().getString()));
        if (component != null) {
            player.sendMessage(component);
        }
    }
}
