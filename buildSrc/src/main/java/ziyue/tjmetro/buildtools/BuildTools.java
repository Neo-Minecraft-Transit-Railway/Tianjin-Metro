package ziyue.tjmetro.buildtools;

public class BuildTools {
	public final String minecraftVersion;
	public final int javaLanguageVersion;
	public final Dependencies dependencies;

	public BuildTools(String minecraftVersion) {
		this.minecraftVersion = minecraftVersion;
		javaLanguageVersion = switch (minecraftVersion) {
			case "1.21.11", "1.21.4", "1.21.3", "1.21.2", "1.21.1", "1.21" -> 21;
			default -> 17;
		};
		if ("1.21.11".equals(minecraftVersion)) {
			dependencies = new Dependencies(
					"0.19.3",
					"0.141.6+1.21.11",
					"17.0.1-beta.1"
			);
		} else {
			dependencies = new Dependencies("0.19.3", "0.141.6+1.21.11", "17.0.1-beta.1");
		}
		System.out.println(dependencies);
	}

	public record Dependencies(String fabricLoader, String fabricApi, String modMenu) {
		@Override
		public String toString() {
			return "Dependencies[fabricLoader=" + fabricLoader + ", fabricApi=" + fabricApi + ", modMenu=" + modMenu + "]";
		}
	}
}
