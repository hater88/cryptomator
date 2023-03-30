package org.cryptomator.ui.convertvault;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import org.cryptomator.common.vaults.Vault;
import org.cryptomator.cryptofs.VaultConfig;
import org.cryptomator.ui.changepassword.NewPasswordController;
import org.cryptomator.ui.changepassword.PasswordStrengthUtil;
import org.cryptomator.ui.common.DefaultSceneFactory;
import org.cryptomator.ui.common.FxController;
import org.cryptomator.ui.common.FxControllerKey;
import org.cryptomator.ui.common.FxmlFile;
import org.cryptomator.ui.common.FxmlLoaderFactory;
import org.cryptomator.ui.common.FxmlScene;
import org.cryptomator.ui.common.StageFactory;
import org.cryptomator.ui.recoverykey.RecoveryKeyFactory;
import org.cryptomator.ui.recoverykey.RecoveryKeyValidateController;

import javax.inject.Named;
import javax.inject.Provider;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Map;
import java.util.ResourceBundle;

@Module
abstract class ConvertVaultModule {

	//TODO: if this fails, we cannot display an error
	@Provides
	@ConvertVaultWindow
	@ConvertVaultScoped
	static VaultConfig.UnverifiedVaultConfig vaultConfig(@ConvertVaultWindow Vault vault) {
		try {
			return vault.getVaultConfigCache().get();
		} catch (IOException e) {
			return null;
		}
	}

	@Provides
	@ConvertVaultWindow
	@ConvertVaultScoped
	static StringProperty provideRecoveryKeyProperty() {
		return new SimpleStringProperty();
	}

	@Provides
	@ConvertVaultWindow
	@ConvertVaultScoped
	static FxmlLoaderFactory provideFxmlLoaderFactory(Map<Class<? extends FxController>, Provider<FxController>> factories, DefaultSceneFactory sceneFactory, ResourceBundle resourceBundle) {
		return new FxmlLoaderFactory(factories, sceneFactory, resourceBundle);
	}

	@Provides
	@ConvertVaultWindow
	@ConvertVaultScoped
	static Stage provideStage(StageFactory factory, @Named("convertVaultOwner") Stage owner, ResourceBundle resourceBundle) {
		Stage stage = factory.create();
		stage.setResizable(false);
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(owner);
		return stage;
	}

	@Provides
	@FxmlScene(FxmlFile.CONVERTVAULT_HUBTOLOCAL_START)
	@ConvertVaultScoped
	static Scene provideHubToLocalStartScene(@ConvertVaultWindow FxmlLoaderFactory fxmlLoaders) {
		return fxmlLoaders.createScene(FxmlFile.CONVERTVAULT_HUBTOLOCAL_START);
	}

	@Provides
	@FxmlScene(FxmlFile.CONVERTVAULT_HUBTOLOCAL_CONVERT)
	@ConvertVaultScoped
	static Scene provideHubToLocalConvertScene(@ConvertVaultWindow FxmlLoaderFactory fxmlLoaders) {
		return fxmlLoaders.createScene(FxmlFile.CONVERTVAULT_HUBTOLOCAL_CONVERT);
	}

	@Provides
	@FxmlScene(FxmlFile.CONVERTVAULT_HUBTOLOCAL_SUCCESS)
	@ConvertVaultScoped
	static Scene provideHubToLocalSuccessScene(@ConvertVaultWindow FxmlLoaderFactory fxmlLoaders) {
		return fxmlLoaders.createScene(FxmlFile.CONVERTVAULT_HUBTOLOCAL_SUCCESS);
	}

	// ------------------

	@Binds
	@IntoMap
	@FxControllerKey(HubToLocalStartController.class)
	abstract FxController bindHubToLocalStartController(HubToLocalStartController controller);

	@Binds
	@IntoMap
	@FxControllerKey(HubToLocalConvertController.class)
	abstract FxController bindHubToLocalConvertController(HubToLocalConvertController controller);

	@Binds
	@IntoMap
	@FxControllerKey(HubToLocalSuccessController.class)
	abstract FxController bindHubToLocalSuccessController(HubToLocalSuccessController controller);


	@Provides
	@IntoMap
	@FxControllerKey(NewPasswordController.class)
	static FxController provideNewPasswordController(ResourceBundle resourceBundle, PasswordStrengthUtil strengthRater) {
		return new NewPasswordController(resourceBundle, strengthRater);
	}

	@Provides
	@IntoMap
	@FxControllerKey(RecoveryKeyValidateController.class)
	static FxController bindRecoveryKeyValidateController(@ConvertVaultWindow Vault vault, @ConvertVaultWindow VaultConfig.UnverifiedVaultConfig vaultConfig, @ConvertVaultWindow StringProperty recoveryKey, RecoveryKeyFactory recoveryKeyFactory) {
		return new RecoveryKeyValidateController(vault, vaultConfig, recoveryKey, recoveryKeyFactory);
	}

}
