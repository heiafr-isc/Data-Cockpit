from lxml import etree as ET
from packaging.version import Version, InvalidVersion
import sys

# Path to the parent POM file
parent_pom_path = 'pom.xml'

# Function to extract the parent version
def extract_version(root):
    version_tag = root.find(f".//{{http://maven.apache.org/POM/4.0.0}}version")
    return version_tag.text if version_tag is not None else None

# Function to update the parent POM version
def update_parent_version(new_version, tree, root):
    version_tag = root.find(f".//{{http://maven.apache.org/POM/4.0.0}}version")
    if version_tag is not None:
        version_tag.text = new_version
        tree.write(parent_pom_path, xml_declaration=True, encoding='UTF-8', pretty_print=True)

# Function to increment the version
def increment_version(version, level):
    version = Version(version)
    if level == 'patch':
        new_version = Version(f"{version.major}.{version.minor}.{version.micro + 1}")
    elif level == 'minor':
        new_version = Version(f"{version.major}.{version.minor + 1}.0")
    elif level == 'major':
        new_version = Version(f"{version.major + 1}.0.0")
    else:
        raise ValueError(f"Invalid level: {level}")
    return str(new_version)

# Main script execution
def main(increment_level):
    # Load the parent POM XML
    tree = ET.parse(parent_pom_path)
    root = tree.getroot()

    # Load current parent version
    current_parent_version = extract_version(root)
    if not current_parent_version:
        print("Parent version not found.")
        return

    # Determine new parent version based on the specified level
    new_parent_version = increment_version(current_parent_version, increment_level)
    # Update the parent POM version
    update_parent_version(new_parent_version, tree, root)
    # Print the new version in a structured way for GitHub Actions
    print(new_parent_version)

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python update_parent_version.py [patch|minor|major]")
        sys.exit(1)
    
    increment_level = sys.argv[1]
    if increment_level not in ['patch', 'minor', 'major']:
        print("Invalid increment level. Use one of: patch, minor, major")
        sys.exit(1)

    main(increment_level)
