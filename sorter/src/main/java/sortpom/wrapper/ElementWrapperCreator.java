package sortpom.wrapper;

import org.jdom.Element;
import sortpom.parameter.DependencySortOrder;
import sortpom.parameter.PluginParameters;
import sortpom.wrapper.content.*;

import static sortpom.wrapper.ElementUtil.*;

/**
 * @author bjorn
 * @since 2012-05-19
 */
public class ElementWrapperCreator {
    private DependencySortOrder sortDependencies;
    private DependencySortOrder sortPlugins;
    private boolean sortProperties;
    private final ElementSortOrderMap elementNameSortOrderMap;


    public ElementWrapperCreator(ElementSortOrderMap elementNameSortOrderMap) {
        this.elementNameSortOrderMap = elementNameSortOrderMap;
    }

    public void setup(PluginParameters pluginParameters) {
        this.sortDependencies = pluginParameters.sortDependencies;
        this.sortPlugins = pluginParameters.sortPlugins;
        this.sortProperties = pluginParameters.sortProperties;
    }

    public Wrapper<Element> createWrapper(Element element) {
        boolean sortedBySortOrderFile = elementNameSortOrderMap.containsElement(element);
        if (sortedBySortOrderFile) {
            if (isDependencyElement(element)) {
                DependencySortedWrapper dependencySortedWrapper = new DependencySortedWrapper(element, elementNameSortOrderMap.getSortOrder(element));
                dependencySortedWrapper.setSortOrder(sortDependencies);
                return dependencySortedWrapper;
            }
            if (isPluginElement(element)) {
                PluginSortedWrapper pluginSortedWrapper = new PluginSortedWrapper(element, elementNameSortOrderMap.getSortOrder(element));
                pluginSortedWrapper.setSortOrder(sortPlugins);
                return pluginSortedWrapper;
            }
            return new SortedWrapper(element, elementNameSortOrderMap.getSortOrder(element));
        }
        if (isPropertyElement(element)) {
            return new AlphabeticalSortedWrapper(element);
        }
        return new UnsortedWrapper<>(element);
    }

    private boolean isDependencyElement(final Element element) {
        if (sortDependencies.isNoSorting()) {
            return false;
        }
        return isElementName(element, "dependency") && isElementParentName(element, "dependencies");
    }

    private boolean isPluginElement(final Element element) {
        if (sortPlugins.isNoSorting()) {
            return false;
        }
        if (isElementName(element, "plugin")) {
            return isElementParentName(element, "plugins") || isElementParentName(element, "reportPlugins");
        }
        return false;
    }

    private boolean isPropertyElement(final Element element) {
        if (!sortProperties) {
            return false;
        }
        String deepName = getDeepName(element);
        boolean inTheRightPlace = deepName.startsWith("/project/properties/")
                || deepName.startsWith("/project/profiles/profile/properties/");
        return inTheRightPlace && isElementParentName(element, "properties");
    }

}
