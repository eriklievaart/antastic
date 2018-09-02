package com.eriklievaart.antastic.model;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.eriklievaart.antastic.config.ProjectLocation;
import com.eriklievaart.toolkit.io.api.ini.IniNode;
import com.eriklievaart.toolkit.io.api.ini.IniNodeIO;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.logging.api.LogTemplate;

public abstract class AbstractFileManager {
	private static final String NODE_NAME = "location";
	private static final String FILE_PROPERTY = "file";
	private static final String NAME_PROPERTY = "name";

	protected LogTemplate log = new LogTemplate(getClass());

	protected Set<ProjectLocation> files = NewCollection.set();
	private File dataFile;

	public AbstractFileManager(File configFile) throws IOException {
		this.dataFile = configFile;
		load();
	}

	protected void load() throws IOException {
		files.clear();
		loadFiles(dataFile);
	}

	protected void loadFiles(File file) throws IOException {
		log.info("loading paths from %", file);
		if (!file.exists()) {
			log.warn("File % not found!", file);
			return;
		}
		List<IniNode> nodes = IniNodeIO.read(file);
		files.addAll(nodes.stream().map(AbstractFileManager::toFileLocation).collect(Collectors.toSet()));
	}

	private static ProjectLocation toFileLocation(IniNode node) {
		ProjectLocation location = new ProjectLocation(node.getProperty(NAME_PROPERTY),
				new File(node.getProperty(FILE_PROPERTY)));
		return location;
	}

	public void addEntry(ProjectLocation file) throws IOException {
		log.info("Adding file: " + file.getName());
		boolean isNew = files.add(file);
		if (!isNew) {
			throw new IOException("File already exists!");
		}
		save();
	}

	public void removeEntry(ProjectLocation remove) throws IOException {
		boolean removed = files.remove(remove);
		Check.isTrue(removed, "Failed to remove $", remove);
		save();
	}

	public void save() throws IOException {
		log.info("saving in $ => $", dataFile, files);

		List<IniNode> nodes = files.stream().map(location -> {
			IniNode node = new IniNode(NODE_NAME);
			node.setProperty(NAME_PROPERTY, location.getName());
			node.setProperty(FILE_PROPERTY, location.getFile().getAbsolutePath());
			return node;

		}).collect(Collectors.toList());

		IniNodeIO.write(nodes, dataFile);
	}
}
