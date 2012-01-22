package org.spoutcraft.launcher.api.skin;

import java.io.InputStream;
import java.io.Reader;
import java.util.Map;

import org.spoutcraft.launcher.api.skin.exceptions.InvalidDescriptionFileException;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

public class SkinDescriptionFile {
	
	private static final Yaml yaml = new Yaml(new SafeConstructor());
	private String name;
	private String version;
	private String fullname;
	private String main;
	private String description;
	private String author;
	private String website;
	private String icon;

	public SkinDescriptionFile(String name, String version, String main) {
		this.name = name;
		this.version = version;
		this.main = main;
	}
	
	@SuppressWarnings("unchecked")
	public SkinDescriptionFile(InputStream stream) throws InvalidDescriptionFileException {
		load((Map<String, Object>) yaml.load(stream));
	}

	@SuppressWarnings("unchecked")
	public SkinDescriptionFile(Reader reader) throws InvalidDescriptionFileException {
		load((Map<String, Object>) yaml.load(reader));
	}

	@SuppressWarnings("unchecked")
	public SkinDescriptionFile(String raw) throws InvalidDescriptionFileException {
		load((Map<String, Object>) yaml.load(raw));
	}

	private void load(Map<String, Object> map) throws InvalidDescriptionFileException {
		try {
			this.name = (String) map.get("name");

			if (!this.name.matches("^[A-Za-z0-9 _.-]+$")) {
				throw new InvalidDescriptionFileException("The field 'name' in skin.yml contains invalid characters.");
			}
		} catch (NullPointerException ex) {
			throw new InvalidDescriptionFileException(ex, "The field 'name' is not defined in the skin.yml!");
		} catch (ClassCastException ex) {
			throw new InvalidDescriptionFileException(ex, "The field 'name' is of the wrong type in the skin.yml!");
		}

		try {
			this.main = (String) map.get("main");
		} catch (NullPointerException ex) {
			throw new InvalidDescriptionFileException(ex, "The field 'main' is not defined in the skin.yml!");
		} catch (ClassCastException ex) {
			throw new InvalidDescriptionFileException(ex, "The field 'main' is of the wrong type in the skin.yml!");
		}

		try {
			this.version = map.get("version").toString();
		} catch (NullPointerException ex) {
			throw new InvalidDescriptionFileException(ex, "The field 'version' is not defined in the skin.yml!");
		} catch (ClassCastException ex) {
			throw new InvalidDescriptionFileException(ex, "The field 'version' is of the wrong type in the skin.yml!");
		}
		
		this.fullname = new StringBuilder().append(name).append(" v").append(version).toString();

		if (map.containsKey("author")) {
			try {
				this.author = (String) map.get("author");
			} catch (ClassCastException ex) {
				throw new InvalidDescriptionFileException(ex, "The field 'author' is of the wrong type in the skin.yml!");
			}
		}

		if (map.containsKey("description")) {
			try {
				this.description = (String) map.get("description");
			} catch (ClassCastException ex) {
				throw new InvalidDescriptionFileException(ex, "The field 'description' is of the wrong type in the skin.yml!");
			}
		}

		if (map.containsKey("website")) {
			try {
				this.website = (String) map.get("website");
			} catch (ClassCastException ex) {
				throw new InvalidDescriptionFileException(ex, "The field 'website' is of the wrong type in the skin.yml!");
			}
		}

		if (map.containsKey("icon")) {
			try {
				this.icon = (String) map.get("icon");
			} catch (ClassCastException ex) {
				throw new InvalidDescriptionFileException(ex, "The field 'icon' is of the wrong type in the skin.yml!");
			}
		}

	}
	
	
	public String getMain() {
		return main;
	}

	public void setMain(String main) {
		this.main = main;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public String getFullName() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

}
