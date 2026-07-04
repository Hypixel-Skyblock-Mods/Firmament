package moe.nea.firmod.api.v1;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Methods you can call to get information about firmods current state.
 */
@ApiStatus.NonExtendable
public abstract class FirmodAPI {
	private static @Nullable FirmodAPI INSTANCE;

	/**
	 * @return the canonical instance of the {@link FirmodAPI}.
	 */
	public static FirmodAPI getInstance() {
		if (INSTANCE != null)
			return INSTANCE;
		try {
			return INSTANCE = (FirmodAPI) Class.forName("moe.nea.firmod.impl.v1.FirmodAPIImpl")
				.getField("INSTANCE")
				.get(null);
		} catch (IllegalAccessException | NoSuchFieldException | ClassCastException e) {
			throw new RuntimeException("Firmod API implementation class found, but could not load api instance.", e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Could not find Firmod API, check FabricLoader.getInstance().isModLoaded(\"firmod\") first.");
		}
	}

	/**
	 * @return list-view of registered extensions
	 */
	public abstract List<? extends FirmodExtension> getExtensions();

	/**
	 * Obtain a reference to the currently hovered item widget, which may be either in the item list or placed in a UI.
	 * This widget may or may not also be present in the Widgets on the current screen.
	 *
	 * @return the currently hovered firmod item widget.
	 */
	public abstract Optional<FirmodItemWidget> getHoveredItemWidget();
}
