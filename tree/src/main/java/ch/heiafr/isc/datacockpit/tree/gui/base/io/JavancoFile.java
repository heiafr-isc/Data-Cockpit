/*
 * This file is part of one of the Data-Cockpit libraries.
 * 
 * Copyright (C) 2024 ECOLE POLYTECHNIQUE FEDERALE DE LAUSANNE (EPFL)
 * 
 * Author - Sébastien Rumley (sebastien.rumley@hefr.ch)
 * 
 * This open source release is made with the authorization of the EPFL,
 * the institution where the author was originally employed.
 * The author is currently affiliated with the HEIA-FR, which is the actual publisher.
 * 
 * The Data-Cockpit program is free software, you can redistribute it and/or modify
 * it under the terms of GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The Data-Cockpit program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * Contributor list -
 */
package ch.heiafr.isc.datacockpit.tree.gui.base.io;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import ch.heiafr.isc.datacockpit.general_libraries.logging.Logger;
import ch.heiafr.isc.datacockpit.tree.gui.base.Javanco;

public class JavancoFile extends File {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static Logger logger;

	static {
		initLogger();	
		System.setProperty(Javanco.JAVANCO_LAUNCH_DIR_PROPERTY,System.getProperty("user.dir"));
		/*if (JavancoJarStarter.startedFromJar()) {
			try {
				System.setProperty("user.dir",(new File(JavancoJarStarter.getRoot()).getAbsolutePath()));
			}
			catch (Exception e) {
				logger.error("Exception in JavancoFile static init " + e.toString());
			}
		}*/
	}
	
	private static void initLogger() {
		 logger = new Logger(JavancoFile.class);
	}

	/*	private static String userDirBackup;	*/

	public JavancoFile(String pathRelativeToJavancoHome) {
		//	super(testPath(pathRelativeToJavancoHome));
		super(pathRelativeToJavancoHome);
		/*	if (Javanco.loggerLoaded()) {
			if (logger == null) {
				logger = new Logger(JavancoFile.class);
			}
			logger.trace("File created " + pathRelativeToJavancoHome);
		} else {
			consoleLog("File created " + pathRelativeToJavancoHome);
		}*/
	}

	public JavancoFile(java.net.URI uri) {
		super(uri);
	}


	@Override
	public String getAbsolutePath() {
		if (!this.isAbsolute()) {
			String toReturn = Javanco.getProperty(Javanco.JAVANCO_HOME_ABS_PROPERTY)+ File.separator + super.getPath() /*+ "/"*/;
			return toReturn;
		}
		return super.getAbsolutePath();
	}

	@Override
	public JavancoFile[] listFiles() {
		File[] f = super.listFiles();
		JavancoFile[] tab = new JavancoFile[f.length];
		for (int i = 0 ; i < f.length ; i++) {
			tab[i] = new JavancoFile(f[i].getAbsolutePath());
		}
		return tab;
	}

	public static void initDefaultOutputDir(String s) throws IOException {
		if (s == null) {
			if (Javanco.getProperty(Javanco.JAVANCO_DEFAULT_OUTPUTDIR_PROPERTY) == null) {
				try {
					System.setProperty(Javanco.JAVANCO_DEFAULT_OUTPUTDIR_PROPERTY, System.getProperty("user.dir"));
				}
				catch (java.security.AccessControlException e) {
					logger.error("Exception in default output dir init " + e);
				}
			}
		} else {
			System.setProperty(Javanco.JAVANCO_DEFAULT_OUTPUTDIR_PROPERTY, s);
		}
		initLogger();
		logger.info("Output dir set to : ");
		logger.info("-->   " + Javanco.getProperty(Javanco.JAVANCO_DEFAULT_OUTPUTDIR_PROPERTY));
	}

	private static String formerUserDir = null;

	private static void changeUserDir(boolean b) {
		initLogger();
		try {
			/*if (JavancoJarStarter.startedFromJar()) {
				String s = (new File(JavancoJarStarter.getRoot()).getAbsolutePath());
				System.setProperty("user.dir",s);
				logger.info("user.dir   set to : " + s);
				return;
			}*/
			if (b) {
				formerUserDir = System.getProperty("user.dir");
				String s = System.getProperty(Javanco.JAVANCO_HOME_ABS_PROPERTY);
				logger.info("user.dir   set to : " + s);				
				System.setProperty("user.dir", s);
			} else {
				logger.info("user.dir   RESTORED to : " + formerUserDir);				
				System.setProperty("user.dir", formerUserDir);
			}
		}
		catch (java.security.AccessControlException e) {
		}
		catch(Exception e) {
			logger.error("Exception in user dir changing " + e);
		}
	}


	public static URL findRessource(String pathRelativeToJavancoHome) throws Exception {
		return findRessource(pathRelativeToJavancoHome, true);
	}

	/**
	 * Get a ressource. First, the ressource is searched using the ClassLoader.
	 * If no resulting file is found, the path is directly used to locate the
	 * path (relatively to JAVANCO_HOME or using absolute reference). If no
	 * ressource can still be found, null is returned
	 * 
	 * @param path Path toward the file. Can be relative or absolute.
	 * @param javancoHomeRelativeOrAbsolute <code>true</code> if given path is relative,
	 * <code>false</code> otherwise
	 */
	public static URL findRessource(String path, boolean javancoHomeRelativeOrAbsolute) throws Exception {
		initLogger();
		if (!Javanco.hasBeenInitialised()) {
			Javanco.initJavanco();
			// throw new IllegalStateException("Javanco not initialised yet, call init method first");
		}
		/*if (JavancoJarStarter.startedFromJar()) {
			JavancoJarStarter.extractCurrentJarLocation(path);
			return (new JavancoFile(path)).getCanonicalFile().toURI().toURL();
		}*/
		if (javancoHomeRelativeOrAbsolute) {
			URL url = ClassLoader.getSystemResource(path);
			if (url != null) {
				logger.debug("Found file (using ClassLoader) : -->  " + url);
				return url;
			} else {
				String javancoHome = Javanco.getProperty(Javanco.JAVANCO_HOME_PROPERTY);
				JavancoFile currentdir = new JavancoFile(javancoHome);
				JavancoFile ressourceFile = new JavancoFile(currentdir.getPath() + File.separator + path);
				if (ressourceFile.exists()) {
					url = ressourceFile.toURI().toURL();
					logger.debug("Found file -->  " + url );
					return url;
				} else {
					if (currentdir.getParentFile() != null) {
						synchronized (System.out) {
							logger.warn("\r\nWARNING: Unable to find file, which is supposed to be : ");
							logger.warn("\r\n-->  " + ressourceFile.getAbsolutePath());
						}
						currentdir = new JavancoFile(currentdir.getParent());
					}
				}
				throw new IllegalStateException("Cannot find ressource " + path);
				//	return null;
			}
		} else {
			JavancoFile ressourceFile = new JavancoFile(path);
			if (ressourceFile.exists()) {
				return ressourceFile.toURI().toURL();
			} else {
				throw new IllegalStateException("Cannot find ressource " + path);

				//	return null;
			}
		}
	}

	public static void findAndProcessPropertiesFile(String path) throws Exception {
		File f = new File(path);
		if (f.exists()) {
			processPropertiesFile(f.toURI().toURL());
		} else {
			processPropertiesFile(findRessource(path));
		}
	}

	public static void processPropertiesFile(URL url) throws Exception {
		initLogger();
		changeUserDir(true);
		java.util.Properties prop = new java.util.Properties();
		logger.info("Processing properties file : ");
		logger.info("-->   " + url);
		prop.loadFromXML(url.openStream());
		//	consoleLog("\r\nJavancoHome : " + javancoHome);
		for (java.util.Enumeration e = prop.propertyNames() ; e.hasMoreElements() ;) {
			String name = (String)e.nextElement();
			String property = prop.getProperty(name);
			URL url2 = null;
			logger.debug("Processing property " + name + " = " + property);
			//		property = property.replace("%JAVANCO_HOME%",javancoHome);
			if (name.contains("toCreate")) {
				JavancoFile dir = new JavancoFile(property + "/");
				logger.debug("toCreate : Creating dirs toward " + dir);
				dir.mkdirs();
				name = name.replace(".toCreate","");
			}
			if (name.contains("toURL")) {

				url2 = findRessource(property);
				logger.debug("toURL :   corresponding ressource is " + url2);
				if (url2 != null) {
					property = url2.toString();
				}
				name = name.replace(".toURL","");
			}
			if (name.endsWith("Dir")) {
				try {
					JavancoFile dir;
					if (url2 != null) {
						dir = new JavancoFile(url2.toURI());
					} else {
						if (property.equals(".")) {
							dir = new JavancoFile("");
						} else {
							dir = new JavancoFile(property);
						}
					}
					if (dir.exists() == false) {
						dir.getAbsoluteFile().mkdirs();
					}
					logger.debug("Dir   : creating dirs toward : " + dir);
					property = dir.getAbsolutePath();
				}
				catch (java.security.AccessControlException eee) {
					property = getDefaultOutputDir() + property;
					logger.warn("Cannot create " + name + " for security reasons.");
					logger.warn("Set unstead at " + property);
				}
			}
			logger.debug("Property " + name + " set to : " + property);
			System.setProperty(name, property);
		}
		changeUserDir(false);
	}

	public static String getDefaultOutputDir() {
		logger.debug("Trying to find output directory");
		String[] locations = {"ch.epfl.javanco.defaultOutputDir", "ch.epfl.javanco.startDir", "user.dir"};
		return internalGetDir(locations);
	}

	private static String internalGetDir(String[] locations) {
		for (String loc : locations) {
			logger.debug("Looking for property " + loc);
			String currentDir = System.getProperty(loc);
			if (currentDir != null) {
				logger.debug("Corresponding dir is : " + currentDir);
				JavancoFile dirr = new JavancoFile(currentDir);
				if (dirr.isAbsolute()) {
					String ret = dirr.getAbsolutePath() + "/";
					logger.debug("--> Final dir " + ret);
					return ret;
				} else {
					logger.debug("Directory isn't absolute, turning it into absolute one");
					dirr = new JavancoFile(System.getProperty("JAVANCO_HOME") + currentDir);
					if (dirr.exists()) {
						String ret = dirr.getAbsolutePath() + "/";
						logger.debug("--> Final directory : " + ret);
						return ret;
					} else {
						logger.debug("No absolute dir at given adress : " + dirr);
					}					
				}

			} else {
				logger.debug("No corresponding dir");
			}
		}
		String ret = new JavancoFile("").getAbsolutePath() + "/";
		logger.debug("No dir found, returning HOME : " + ret);
		return ret;
	}
}
