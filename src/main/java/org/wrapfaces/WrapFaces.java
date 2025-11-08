package org.wrapfaces;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import org.primefaces.behavior.ajax.AjaxBehaviorListenerImpl;
import org.primefaces.component.tree.UITreeNode;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.TreeDragDropEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import jakarta.annotation.PostConstruct;
import jakarta.el.MethodExpression;
import jakarta.el.ValueExpression;
import jakarta.faces.application.Application;
import jakarta.faces.component.ActionSource;
import jakarta.faces.component.EditableValueHolder;
import jakarta.faces.component.PartialStateHolder;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIComponentBase;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.component.ValueHolder;
import jakarta.faces.component.behavior.AjaxBehavior;
import jakarta.faces.component.behavior.Behavior;
import jakarta.faces.component.html.HtmlColumn;
import jakarta.faces.component.html.HtmlCommandButton;
import jakarta.faces.component.html.HtmlDataTable;
import jakarta.faces.component.html.HtmlForm;
import jakarta.faces.component.html.HtmlInputText;
import jakarta.faces.component.html.HtmlOutputText;
import jakarta.faces.component.html.HtmlPanelGrid;
import jakarta.faces.component.html.HtmlPanelGroup;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AbortProcessingException;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.event.ActionListener;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.event.AjaxBehaviorListener;
import jakarta.faces.event.ValueChangeEvent;
import jakarta.faces.event.ValueChangeListener;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

// - https://www.tutorialspoint.com/jsf/jsf_basic_tags.htm
// - https://www.tutorialspoint.com/jsf/jsf_display_datatable.htm
// - http://e-ammar.net/Oracle_TIPS/adding_jsf_components_dynamicall.htm
// - https://cagataycivici.wordpress.com/2005/12/13/dynamically_adding_components_to_a/
// - https://illegalargumentexception.blogspot.com/2011/12/jsf-mocking-facescontext-for-unit-tests.html
// - https://github.com/BerndMuller/faces4/tree/main/src/main/java/de/pdbm/faces4
@Named
@ViewScoped
public class WrapFaces implements Serializable {

	private static final long serialVersionUID = 1L;

	// --- Entry Point for View creation ---

	private HtmlForm form;

	@PostConstruct
	public void init() {

		User user = new User("admin", new Credential("secret123"));

		form = user.displayFrom();

	}

	public HtmlForm getForm() { return form; }

	public void setForm(HtmlForm form) {/* this.form = form; */}

	//
	// objects / Model (Must be Serializable for ViewScoped/SessionScoped)
	//

	public static class User implements Serializable {

		private static final long serialVersionUID = 1L;
		private String name; // Does not need to be final if it is to be updated
		private Credential credential;

		public User(String name, Credential credential) {
			this.name = name;
			this.credential = credential;
		}

		public Credential credential() { return credential; }

		// Constructor for mapping the values from the Component back into the object
		public User(Map<String, Object> map) {
			// delegates the mapping to the // nested object
			this(map.getOrDefault("txtUser", "").toString(), new Credential(map));
		}

		// The method that displays itself as UI components

		public Form displayFrom() {

			// PanelGroup for user input
			PanelGrid<User> userGrid = new PanelGrid<User>("userGrid")
					// Use a 2-column layout (default for PanelGrid)
					// NEUE, VEREINFACHTE SYNTAX DANK ÜBERLADENER addRow-METHODE
					.addRow(new Label("lblUser", "User:"), new Text("txtUser", this.name))
					.addRow(new Label("lblSecret", "Secret:"), credential.displayInput())
					// Defines the mapping function that converts the Map back into a User object
					.map(User::new); // Uses the Map constructor above

			// PanelGroup for the buttons
			PanelGroup buttonGroup = new PanelGroup("btnGrp",
					new Button("btnCancel", "Abbrechen").onAction(e -> System.out.println("Canceled.")),
					new Button("btnSubmit", "Senden").onAction(e -> {
						// Here, the new User model is retrieved from the UI values
						User updatedUser = userGrid.model(); // Use userGrid here
						System.out.println("Submitted. Updated User: " + updatedUser.toString());
						updatedUser.credential.authenticate();
					})
			);

			// table for users
			Table<User> table = new Table<>("tblUser", Arrays.asList(this), new Table.Column<>(User.class, "", "User"),
					new Table.Column<>(Credential.class, "credential()", "Secret"));

			Form form1 = new Form() {

				private static final long serialVersionUID = 1L;

				{
					add(new Label("helloMessage", "Hello World") {

						private static final long serialVersionUID = 1L;

						@Override
						public void render(FacesContext context, HtmlOutputText uiComponent) {
							super.render(context, uiComponent);
							// Wicket: tag.setName("span");
							// Wicket: tag.put("style", "font-weight:bold");
							uiComponent.getPassThroughAttributes().put("style", "font-weight:bold");
						}
					});
				}
			};

			return new Form("loginForm", userGrid, buttonGroup, table);
		}

		// No!!! Getter/Setter like JSF beans - not OOP conform
		// for EL binding present object with toString() and use hashCode for id.

		@Override
		public String toString() { return "name:" + name + ", credential:" + credential; }

		@Override
		public int hashCode() { return Objects.hash(getClass().getSimpleName(), name); }

		@Override
		public boolean equals(Object obj) { return obj != null && hashCode() == obj.hashCode(); }
	}

	public static class Credential implements Serializable {

		private static final long serialVersionUID = 1L;
		private String value;

		public Credential(String value) { this.value = value; }

		// Constructor for mapping the value from the Component Map back into the object
		public Credential(Map<String, Object> map) { this(map.getOrDefault("txtSecret", "").toString()); }

		public void authenticate() {
			// Simulate authentication
			System.out.println("Authenticating with secret: " + value);
		}

		// The method that displays itself as a UI component
		public Text displayInput() { return new Text("txtSecret", value); }

		// No!!! Getter/Setter like JSF beans - not OOP conform
		// for EL binding present object with toString() and hashCode for id.

		@Override
		public String toString() { return "*****"; }

		@Override
		public int hashCode() { return Objects.hash(getClass().getSimpleName(), value); }

		@Override
		public boolean equals(Object obj) { return obj != null && hashCode() == obj.hashCode(); }
	}

	//
	// component framework / Core Wrapper Classes
	//

	public static abstract class HtmlFormWrap extends HtmlForm implements WrapComponent<HtmlForm>, Serializable {

		private static final long serialVersionUID = 1L;
		private String id;
		private boolean initialized;
		private String styleClass = "w-form";

		private List<UIComponent> components;

		public HtmlFormWrap() { /* For JSF State Saving */ this.components = new ArrayList<>(0); }

		public HtmlFormWrap(String id, UIComponent... components) { this(id, "w-form", components); }

		public HtmlFormWrap(String id, String styleClass, UIComponent... components) {
			this.id = id;
			this.styleClass = styleClass;
			this.components = new ArrayList<>(Arrays.asList(components));
			this.setTransient(true);
		}

		@Override // --- JSF LIFECYCLE HOOKS ---
		public void encodeBegin(FacesContext context) throws IOException {
			if (!initialized) {
				initialize(context, this);
			}
			if (!this.isRendered()) {
				return; // skip rendering
			}
			head(context, context.getViewRoot());
			render(context, this);
			super.encodeBegin(context);
		}

		// -- called immediately before starts rendering
		protected void initialize(FacesContext context, UIComponent uiComponent, UIComponent... components) {
			if (this.id != null) {
				uiComponent.setId(id);
			}
			this.getPassThroughAttributes().put("class", styleClass);
			for (UIComponent uiChildComponent : components) {
				uiComponent.getChildren().add(uiChildComponent);
			}
		}

		/* Fluent API for add-Method */
		public HtmlFormWrap add(UIComponent component) {
			this.components.add(component);
			return this;
		}

		protected void head(FacesContext context, UIViewRoot root) {/* can override this method */}

		@Override
		public void render(FacesContext context, HtmlForm uiComponent) {/* can override this method */}

	}

	/**
	 * Concrete Form, encapsulation of the HtmlForm container.
	 */
	public static class Form extends HtmlFormWrap implements Serializable {

		private static final long serialVersionUID = 1L;

		public Form() { super(); }

		public Form(String id, UIComponent... components) { super(id, components); }

		public Form(String id, String styleClass, UIComponent... components) { super(id, styleClass, components); }

		/**
		 * Fluent API-Methode, die den konkreten Typ (Form) für das Chaining zurückgibt.
		 */
		@Override
		public Form add(UIComponent component) {
			super.add(component);
			return this;
		}
	}

	// Encapsulation of the HtmlPanelGroup container with generic mapping

	// --- PANEL GROUP ---

	public static abstract class HtmlPanelGroupWrap extends HtmlPanelGroup
			implements WrapComponent<HtmlPanelGroup>, Serializable {

		private static final long serialVersionUID = 1L;
		private String id;
		private boolean initialized;
		private String styleClass;
		private List<UIComponent> children = new ArrayList<>();

		public HtmlPanelGroupWrap() { this.setTransient(true); }

		public HtmlPanelGroupWrap(String id, UIComponent... components) {
			this.id = id;
			this.children.addAll(Arrays.asList(components));
			this.setTransient(true);
		}

		// Fluent API add-Methode
		public HtmlPanelGroupWrap add(UIComponent component) {
			this.children.add(component);
			return this;
		}

		protected void initialize(FacesContext context, UIComponent uiComponent) {
			if (!initialized) {
				if (this.id != null) {
					uiComponent.setId(id);
				}
				if (this.styleClass != null) {
					uiComponent.getPassThroughAttributes().put("class", styleClass);
				}

				// Überträgt alle Kinder aus der transienten Wrapper-Liste in den JSF-Baum
				for (UIComponent uiChildComponent : children) {
					uiComponent.getChildren().add(uiChildComponent);
				}
				initialized = true;
			}
		}

		// Hooks für Ressourcen und Attribute
		protected void head(FacesContext context, UIViewRoot root) {}

		@Override
		public void render(FacesContext context, HtmlPanelGroup uiComponent) {}

		// Defines the mapping function
//		public PanelGroup<T> map(SerializableFunction<Map<String, Object>, T> mapped) {
//			this.mapped = mapped;
//			return this;
//		}
//
//		// Extracts values from the child components and maps them to the model
//		public T model() {
//			if (mapped == null) {
//				throw new IllegalStateException("Mapping function has not been set via .map(T::new).");
//			}
//
//			Map<String, Object> values = new HashMap<>();
//
//			// Iterates over all children added via the constructor or .add()
//			for (UIComponent input : this.getChildren()) {
//				// Only consider components with values
//				if (input instanceof jakarta.faces.component.ValueHolder) {
//					values.put(input.getId(), ((jakarta.faces.component.ValueHolder) input).getValue());
//				}
//			}
//			return mapped.apply(values);
//		}
		@Override
		public void encodeBegin(FacesContext context) throws IOException {
			if (!initialized) {
				initialize(context, this);
			}
			if (!this.isRendered()) {
				return;
			}
			head(context, context.getViewRoot());
			render(context, this);
			super.encodeBegin(context);
		}
	}

	public static class PanelGroup extends HtmlPanelGroupWrap implements Serializable {
		private static final long serialVersionUID = 1L;

		public PanelGroup() { super(); }

		public PanelGroup(String id, UIComponent... components) { super(id, components); }

		@Override
		public PanelGroup add(UIComponent component) {
			super.add(component);
			return this;
		}
	}

	// --- 3. TEMPLATE KONZEPT (Wicket-Style WebPage/BasePage) ---

	/**
	 * Abstract Layout.
	 */
	public static abstract class PanelTemplate extends PanelGroup {

		private static final long serialVersionUID = 1L;

		public PanelTemplate(String id) {
			super(id);
			// Fügt die statischen Layout-Bereiche hinzu
			this.add( // Header-Bereich
					createHeader())
					// Layout-Wrapper fuer Menü und Inhalt
					.add(new PanelGroup("layout-area").add(createLeftMenu()).add(createContent())
					// Hook für den Content
					)
					// Footer-Bereich
					.add(createFooter());
		}

		// --- Template-Methoden (Die statischen Bereiche) ---

		protected PanelGroup createHeader() {
			return new PanelGroup("header-area").add(new Label("siteTitle", "WrapFaces Layout Manager"));
		}

		protected PanelGroup createLeftMenu() {
			return new PanelGroup("left-menu-area").add(new Label("menuLink1", "Home"))
					.add(new Label("menuLink2", "About"));
		}

		protected PanelGroup createFooter() {
			return new PanelGroup("footer-area").add(new Label("footerInfo", "© 2025 WrapFaces. All rights reserved."));
		}

		// --- customization Point ---

		private PanelGroup createContent() {
			// // HIER WIRD DER HOOK AUFGERUFEN!
			return new PanelGroup("content-area").add(getContent());
		}

		protected abstract UIComponent getContent();
	}

	// --- 3. PANELGRID IMPLEMENTATION (Für Formulare und Model-Binding) ---

	/**
	 * Encapsulation of HtmlPanelGrid with generic mapping and a grid layout
	 * (emulates p:panelGrid). Uses CSS Grid/Flex for responsive layout.
	 *
	 * @param <T> The model type to which the component values are mapped.
	 */
	public static class PanelGrid<T> extends HtmlPanelGrid implements Serializable {

		private static final long serialVersionUID = 1L;
		private Mapped<Map<String, Object>, T> mapped;
		private final List<Row> rows = new ArrayList<>();
		private int columns = 1; // Default 2 columns

		public PanelGrid() {
			// For JSF State Saving
		}

		public PanelGrid(String id) {
			this.setId(id);
			// Renderer: Set CSS classes for responsive grid layout
			// (using standard CSS or PrimeFlex if available)
			this.getPassThroughAttributes().put("styleClass", "ui-panelgrid ui-fluid ui-grid");
			setColumns(columns);

		}

		/** Represents a row in the grid, which is a collection of columns. */
		public static class Row implements Serializable {
			private static final long serialVersionUID = 1L;
			private final List<Column> columns = new ArrayList<>();

			public Row(Column... columns) { this.columns.addAll(Arrays.asList(columns)); }

			public List<Column> getColumns() { return columns; }
		}

		/**
		 * * Represents a column in the grid, which holds one or more UI components.
		 * Sets layout="block" to force rendering of the wrapper element (usually
		 * <div>).
		 */
		public static class Column extends HtmlPanelGroup implements Serializable {
			private static final long serialVersionUID = 1L;

			public Column(UIComponent... components) {
				this.setTransient(false);
				this.setLayout("block");
				this.getPassThroughAttributes().put("styleClass", "ui-grid-col");
				render(this, components);
			}

			// Renderer: adds children
			private void render(UIComponent uiComponent, UIComponent... components) {
				for (UIComponent uiChildComponent : components) {
					uiComponent.getChildren().add(uiChildComponent);
				}
			}
		}

		// Adds a row to the grid (Fluent API) - Original method
		public PanelGrid<T> addRow(Row row) {
			this.rows.add(row);
			for (Column col : row.getColumns()) {
				this.getChildren().add(col);
			}
			return this;
		}

		public PanelGrid<T> addRow(UIComponent... components) {
			Column[] columns = new Column[components.length];
			for (int i = 0; i < components.length; i++) {
				columns[i] = new Column(components[i]);
			}
			return addRow(new Row(columns));
		}

		// Defines the mapping function
		public PanelGrid<T> map(Mapped<Map<String, Object>, T> mapped) {
			this.mapped = mapped;
			return this;
		}

		// Extracts values from the child components and maps them to the model
		public T model() {
			if (mapped == null) {
				throw new IllegalStateException("Mapping function has not been set via .map(T::new).");
			}
			Map<String, Object> values = new HashMap<>(0);
			// Iterate over all columns (children)
			for (UIComponent col : this.getChildren()) {
				if (col instanceof Column) {
					// Iterate over components within the column
					for (UIComponent input : col.getChildren()) {
						// Only consider components with values (Text/Input)
						if (input instanceof jakarta.faces.component.ValueHolder && input.getId() != null) {
							values.put(input.getId(), ((jakarta.faces.component.ValueHolder) input).getValue());
						}
					}
				}
			}
			return mapped.apply(values);
		}

	}

	// Encapsulation of HtmlOutputText (Label)
	public static abstract class HtmlOutputTextWrap extends HtmlOutputText
			implements WrapComponent<HtmlOutputText>, Serializable {

		private static final long serialVersionUID = 1L;

		private String id;
		private String value;

		private String styleClass = "w-label";

		public HtmlOutputTextWrap() {/* JSF State Saving */}

		public HtmlOutputTextWrap(HtmlOutputText wrapped) {
			this(wrapped.getId(), wrapped.getValue() != null ? wrapped.getValue().toString() : null);
		}

		public HtmlOutputTextWrap(HtmlOutputText wrapped, String styleClass) {
			this(wrapped);
			this.styleClass = styleClass;
		}

		public HtmlOutputTextWrap(String id, String value) {
			this.id = id;
			this.value = value;
			this.setTransient(true);
		}

		protected void initialize(FacesContext context, HtmlOutputText uiComponent) {
			if (this.id != null) {
				uiComponent.setId(id);
			}
			this.getPassThroughAttributes().put("class", styleClass);
			if (uiComponent instanceof ValueHolder) {
				uiComponent.setValue(value);
			}
		}

		protected void head(FacesContext context, UIViewRoot root) {/* can override. */}

		@Override
		public void render(FacesContext context, HtmlOutputText uiComponent) {/* can override. */}

		@Override
		public void encodeBegin(FacesContext context) throws IOException {
			initialize(context, this);
			if (!this.isRendered()) {
				return; // skip rendering
			}
			head(context, context.getViewRoot());
			render(context, this);
			super.encodeBegin(context);
		}

	}

	/**
	 * Concrete Label-Class, encapsulation of HtmlOutputText (Label).
	 */
	public static class Label extends HtmlOutputTextWrap implements Serializable {

		private static final long serialVersionUID = 1L;

		public Label() { super(); }

		public Label(String id, String value) { super(id, value); }

		public Label(Label org, String styleClass) { super(org, styleClass); }

	}

	// Encapsulation of HtmlInputText (Text field)
	public static class Text extends HtmlInputText
			implements WrapComponent<HtmlInputText>, ValueChangeListener, AjaxBehaviorListener, Serializable {
		private static final long serialVersionUID = 1L;

		private final String id;
		private final String initialValue;
		private List<Changed<EventObject, String>> events = new ArrayList<>(0);
		private boolean initialized = false;

		public Text() {
			this.id = null;
			this.initialValue = null;
		}

		public Text(String id, String initialValue) {
			this.id = id;
			this.initialValue = initialValue;
			this.setTransient(true);
		}

		// --- WrapComponent Hook (Called after initialization in encodeBegin) ---

		@Override
		public void render(FacesContext context, HtmlInputText uiComponent) {
			// Can be used for custom renderer logic if needed
		}

		// --- Component Lifecycle (Initialization during render phase) ---

		@Override
		public void encodeBegin(FacesContext context) throws IOException {
			if (!initialized) {
				if (this.id != null) {
					this.setId(this.id);
				}
				if (this.initialValue != null) {
					this.setValue(this.initialValue);
				}
				addValueChange(this, this);
				initialized = true;
			}
			render(context, this);
			super.encodeBegin(context);
		}

		/**
		 * Registriert den ValueChangeListener, falls er noch nicht hinzugefügt wurde.
		 */
		private boolean addValueChange(UIComponent e, ValueChangeListener valueChangeListener) {
			if (e instanceof EditableValueHolder) {
				EditableValueHolder editableValueHolder = (EditableValueHolder) e;
				for (ValueChangeListener listener : editableValueHolder.getValueChangeListeners()) {
					if (listener.getClass().equals(valueChangeListener.getClass())) {
						return true;
					}
				}
				editableValueHolder.addValueChangeListener(valueChangeListener);
			}
			return false;
		}

		// Fluent API for event handlers
		public Text onChanged(Changed<EventObject, String> event) {
			events.add(event);
			return this;
		}

		@Override // ValueChangeListener implementation: delegates to the registered lambdas
		public void processValueChange(ValueChangeEvent event) throws AbortProcessingException {
			events.forEach(e -> e.accept(event, event.getNewValue() != null ? event.getNewValue().toString() : ""));
		}

		@Override // AjaxBehaviorListener implementation: delegates to the registered lambdas
		public void processAjaxBehavior(AjaxBehaviorEvent event) throws AbortProcessingException {
			if (event.getComponent() instanceof EditableValueHolder) {
				Object value = ((EditableValueHolder) event.getComponent()).getValue();
				events.forEach(e -> e.accept(event, value != null ? value.toString() : null));
			}
		}
	}

	// Encapsulation of HtmlCommandButton
	public static class Button extends HtmlCommandButton
			implements WrapComponent<HtmlCommandButton>, ActionListener, Serializable {
		private static final long serialVersionUID = 1L;
		private final String id;
		private final String value;
		private List<Action<EventObject>> events = new ArrayList<>(0);
		private boolean initialized = false;

		public Button() {
			this.id = null;
			this.value = null;
		}

		public Button(String id, String value) {
			this.id = id;
			this.value = value;
			this.setTransient(true);
		}

		// WrapComponent Hook - called in encodeBegin
		@Override
		public void render(FacesContext context, HtmlCommandButton uiComponent) {
			// Can be used for custom renderer logic if needed
		}

		@Override
		public void encodeBegin(FacesContext context) throws IOException {
			if (!initialized) {
				if (this.id != null) {
					this.setId(this.id);
				}
				if (this.value != null) {
					this.setValue(this.value);
				}
				addActionListener(this, this);
				initialized = true;
			}
			render(context, this);
			super.encodeBegin(context);
		}

		// Fluent API for event handlers
		public Button onAction(Action<EventObject> event) {
			events.add(event);
			return this;
		}

		@Override // ActionListener implementation: delegates to the registered lambdas
		public void processAction(ActionEvent event) throws AbortProcessingException {
			events.forEach(e -> e.accept(event));
		}

		private boolean addActionListener(UIComponent e, ActionListener actionListener) {
			if (e == null || !(e instanceof ActionSource)) {
				return true;
			}
			ActionSource actionSource = (ActionSource) e;
			for (ActionListener listener : actionSource.getActionListeners()) {
				if (listener.getClass().equals(actionListener.getClass())) {
					return true;
				}
			}
			actionSource.addActionListener(actionListener);
			return false;
		}
	}

	// -- DataTable

	public static class Table<T> extends HtmlDataTable implements WrapComponent<HtmlDataTable>, Serializable {

		private static final long serialVersionUID = 1L;

		private final String id;
		private final List<T> items;
		private final List<Column<?>> columns = new ArrayList<>();
		private boolean initialized = false;

		public Table() {
			this.id = null;
			this.items = null;
		} // For JSF State Saving

		public Table(String id, List<T> items, Column<?>... initialColumns) {
			this.id = id;
			this.items = items;
			this.setTransient(true);
			this.columns.addAll(Arrays.asList(initialColumns));
		}

		protected void initialize(FacesContext context) {
			if (!initialized) {
				if (this.id != null) {
					this.setId(id);
				}
				this.setVar("item");
				this.setValue(items);
				for (Column<?> column : this.columns) {
					renderColumn(this, column);
				}
				initialized = true;
			}
		}

		@Override
		public void encodeBegin(FacesContext context) throws IOException {
			initialize(context);
			render(context, this); // Hook
			super.encodeBegin(context);
		}

		@Override
		public void render(FacesContext context, HtmlDataTable uiComponent) {}

		protected void renderColumn(HtmlDataTable table, Column<?> column) {
			FacesContext context = FacesContext.getCurrentInstance();
			Application application = context.getApplication();
			// 1. Header (f:facet name="header")
			HtmlOutputText headerText = new HtmlOutputText();
			headerText.setValue(column.getHeader());
			// 2. Column Content dynamic binding
			HtmlOutputText itemText = new HtmlOutputText();
			// EL #{item} or #{item.property}
			String valueExpr = column.getProperty().trim().isEmpty() ? "item" : "item." + column.getProperty();
			// ValueExpression dynamic Data binding
			ValueExpression valueExpression = application.getExpressionFactory()
					.createValueExpression(context.getELContext(), "#{" + valueExpr + "}", column.getPropertyType());
			itemText.setValueExpression("value", valueExpression);
			// 3.
			HtmlColumn htmlColumn = new HtmlColumn();
			htmlColumn.getFacets().put("header", headerText);
			htmlColumn.getChildren().add(itemText);
			table.getChildren().add(htmlColumn);
		}

		// --- Fluent API ---

		public List<T> getItems() { return items; }

		public Table<T> addColumn(Class<?> type, String property) { return addColumn(type, property, property); }

		public Table<T> addColumn(Class<?> type) { return addColumn(type, ""); }

		public Table<T> addColumn(Class<?> type, String property, String header) {
			this.columns.add(new Column<>(type, property, header));
			return this;
		}

		public static class Column<T> implements Serializable {

			private static final long serialVersionUID = 1L;

			private final String header;
			private final String property;
			private Class<?> propertyType = String.class;

			public Column(Class<?> propertyType) { this(propertyType, ""); }

			public Column(Class<?> propertyType, String property) { this(propertyType, property, property); }

			public Column(Class<?> propertyType, String property, String header) {
				this.header = header;
				this.property = property;
				this.propertyType = propertyType;
			}

			public Class<?> getPropertyType() { return propertyType; }

			public String getHeader() { return header; }

			public String getProperty() { return property; }
		}
	}

	// -- tree

	public static class Tree<T> extends org.primefaces.component.tree.Tree
			implements WrapComponent<org.primefaces.component.tree.Tree>, Serializable {

		private static final long serialVersionUID = 1L;

		private final String id;
		private final List<NodeType<?>> nodeTypes;
		private final TreeNode<T> root;

		// Event-Handler
		private TreeNode<T> selectedNode;
		private final List<Changed<EventObject, TreeNode<T>>> selectEvents = new ArrayList<>(0);
		private final List<Changed<EventObject, TreeNode<T>>> contextEvents = new ArrayList<>(0);
		private final List<Changed<EventObject, TreeNode<T>>> unselectEvents = new ArrayList<>(0);
		private final List<Changed<EventObject, TreeNode<T>>> expandEvents = new ArrayList<>(0);
		private final List<Changed<EventObject, TreeNode<T>>> collapseEvents = new ArrayList<>(0);
		private final List<Changed<EventObject, TreeNode<T>>> filterEvents = new ArrayList<>(0);
		private final List<Changed<TreeNode<T>[], TreeNode<T>>> dragDropEvents = new ArrayList<>(0);
		private boolean initialized = false;

		public Tree() {
			this.id = null;
			this.root = null;
			this.nodeTypes = new ArrayList<>(0);
		}

		public Tree(String id, TreeNode<T> root, NodeType<?>... nodeTypes) {
			this.id = id;
			this.root = root;
			this.nodeTypes = Arrays.asList(nodeTypes);
			this.setTransient(true);
		}

		protected void initialize(FacesContext context) {
			if (!initialized) {
				// 1. initial
				this.setId(id);
				this.setVar("item");
				// 2. Wrap Tree Data: Tree need a Root Node, Root-Element as child
				TreeNode<T> rootNode = new DefaultTreeNode<>();
				if (this.root != null) {
					rootNode.getChildren().add(root);
					root.setParent(rootNode);
				}
				this.setValue(rootNode);
				this.setSelectionMode("single");
				// 3. Füge dynamische Nodes (p:treeNode) hinzu
				if (nodeTypes.isEmpty()) {
					// Fallback für einen Default-Node, falls keine angegeben wurden
					renderNodeType(this, new NodeType<T>(String.class, "", "default"));
				} else {
					for (NodeType<?> nodeType : nodeTypes) {
						renderNodeType(this, nodeType);
					}
				}
				// 4. add all PrimeFaces AJAX Behavior for Events
				Ajax.PrimeTreeAjax pTreeAjax = new Ajax.PrimeTreeAjax(FacesContext.getCurrentInstance(), this);
				pTreeAjax.addBehavior("select", new OnNodeSelect<>(selectEvents));
				pTreeAjax.addBehavior("contextMenu", new OnNodeSelect<>(contextEvents));
				pTreeAjax.addBehavior("unselect", new OnNodeUnselect<>(unselectEvents));
				pTreeAjax.addBehavior("expand", new OnNodeExpand<>(expandEvents));
				pTreeAjax.addBehavior("collapse", new OnNodeCollapse<>(collapseEvents));
				pTreeAjax.addBehavior("dragdrop", new OnNodeDragDrop<>(dragDropEvents));
				pTreeAjax.addBehavior("filter", new OnNodeFilter<>(filterEvents));
				initialized = true;
			}
		}

		@Override
		public void encodeBegin(FacesContext context) throws IOException {
			initialize(context);
			render(context, this); // Hook
			super.encodeBegin(context);
		}

		@Override
		public void render(FacesContext context, org.primefaces.component.tree.Tree uiComponent) {
			// Render Hook
		}

		protected void renderNodeType(org.primefaces.component.tree.Tree pTree, NodeType<?> nodeType) {
			FacesContext context = FacesContext.getCurrentInstance();
			Application app = context.getApplication();
			// 1. create Output Text (e.g. <h:outputText value="#{item.property}">)
			HtmlOutputText treeNodeOutput = (HtmlOutputText) app.createComponent(HtmlOutputText.COMPONENT_TYPE);
			// 2. create EL Expression: #{item} or #{item.property}
			String valueExpr = nodeType.getProperty().trim().isEmpty() ? "item" : "item." + nodeType.getProperty();
			ValueExpression valueExpression = context.getApplication()
					.getExpressionFactory()
					.createValueExpression(context.getELContext(), "#{" + valueExpr + "}", nodeType.getPropertyType());
			treeNodeOutput.setValueExpression("value", valueExpression);
			// 3. create p:treeNode of PrimeFaces UITreeNode
			UIComponent textTreeNodeTemplate = app.createComponent(UITreeNode.COMPONENT_TYPE);
			textTreeNodeTemplate.getAttributes().put("type", nodeType.getType());
			if (nodeType.getIcon() != null) {
				textTreeNodeTemplate.getAttributes().put("icon", nodeType.getIcon());
			}
			// 4. add Output-Text to p:treeNode
			textTreeNodeTemplate.getChildren().add(treeNodeOutput);
			// 5. add p:treeNode to p:tree
			pTree.getChildren().add(textTreeNodeTemplate);
		}

		// --- Getter/Setter ---
		public TreeNode<?> getSelectedNode() { return selectedNode; }

		public void setSelectedNode(TreeNode<T> selectedNode) { this.selectedNode = selectedNode; }

		public List<NodeType<?>> getNodeTypes() { return new ArrayList<>(nodeTypes); }

		// --- Fluent API for event handlers ---
		public Tree<T> onSelect(Changed<EventObject, TreeNode<T>> event) {
			selectEvents.add(event);
			return this;
		}

		public Tree<T> onContext(Changed<EventObject, TreeNode<T>> event) {
			contextEvents.add(event);
			return this;
		}

		public Tree<T> onUnselect(Changed<EventObject, TreeNode<T>> event) {
			unselectEvents.add(event);
			return this;
		}

		public Tree<T> onExpand(Changed<EventObject, TreeNode<T>> event) {
			expandEvents.add(event);
			return this;
		}

		public Tree<T> onCollapse(Changed<EventObject, TreeNode<T>> event) {
			collapseEvents.add(event);
			return this;
		}

		public Tree<T> onFilter(Changed<EventObject, TreeNode<T>> event) {
			filterEvents.add(event);
			return this;
		}

		public Tree<T> onDragDrop(Changed<TreeNode<T>[], TreeNode<T>> event) {
			dragDropEvents.add(event);
			return this;
		}

		// --- Utility ---

		public static class NodeType<T> implements Serializable {
			private static final long serialVersionUID = 1L;
			private final String type;
			private final String property;
			private Class<?> propertyType = String.class;
			private String icon;

			public NodeType(Class<?> propertyType, String property, String type) {
				this(propertyType, property, type, null);
			}

			public NodeType(Class<?> propertyType, String property, String type, String icon) {
				this.type = type;
				this.property = property;
				this.propertyType = propertyType;
				this.icon = icon;
			}

			public Class<?> getPropertyType() { return propertyType; }

			public String getType() { return type; }

			public String getIcon() { return icon; }

			public String getProperty() { return property; }
		}

		public static class OnNodeSelect<T> implements AjaxBehaviorListener, Serializable {
			private static final long serialVersionUID = 1L;
			private final List<Changed<EventObject, TreeNode<T>>> selectEvents;

			public OnNodeSelect(List<Changed<EventObject, TreeNode<T>>> selectEvents) {
				this.selectEvents = selectEvents;
			}

			@SuppressWarnings("unchecked")
			@Override
			public void processAjaxBehavior(AjaxBehaviorEvent event) throws AbortProcessingException {
				if (event instanceof NodeSelectEvent && ((NodeSelectEvent) event).getTreeNode() != null) {
					selectEvents.forEach(e -> e.accept(event, ((NodeSelectEvent) event).getTreeNode()));
				}
			}
		}

		public static class OnNodeUnselect<T> implements AjaxBehaviorListener, Serializable {
			private static final long serialVersionUID = 1L;
			private final List<Changed<EventObject, TreeNode<T>>> unselectEvents;

			public OnNodeUnselect(List<Changed<EventObject, TreeNode<T>>> unselectEvents) {
				this.unselectEvents = unselectEvents;
			}

			@SuppressWarnings("unchecked")
			@Override
			public void processAjaxBehavior(AjaxBehaviorEvent event) throws AbortProcessingException {
				if (event instanceof NodeUnselectEvent && ((NodeUnselectEvent) event).getTreeNode() != null) {
					unselectEvents.forEach(e -> e.accept(event, ((NodeUnselectEvent) event).getTreeNode()));
				}
			}
		}

		public static class OnNodeExpand<T> implements AjaxBehaviorListener, Serializable {
			private static final long serialVersionUID = 1L;
			private final List<Changed<EventObject, TreeNode<T>>> expandEvents;

			public OnNodeExpand(List<Changed<EventObject, TreeNode<T>>> expandEvents) {
				this.expandEvents = expandEvents;
			}

			@SuppressWarnings("unchecked")
			@Override
			public void processAjaxBehavior(AjaxBehaviorEvent event) throws AbortProcessingException {
				if (event instanceof NodeExpandEvent && ((NodeExpandEvent) event).getTreeNode() != null) {
					expandEvents.forEach(e -> e.accept(event, ((NodeExpandEvent) event).getTreeNode()));
				}
			}
		}

		public static class OnNodeCollapse<T> implements AjaxBehaviorListener, Serializable {
			private static final long serialVersionUID = 1L;
			private final List<Changed<EventObject, TreeNode<T>>> collapseEvents;

			public OnNodeCollapse(List<Changed<EventObject, TreeNode<T>>> collapseEvents) {
				this.collapseEvents = collapseEvents;
			}

			@SuppressWarnings("unchecked")
			@Override
			public void processAjaxBehavior(AjaxBehaviorEvent event) throws AbortProcessingException {
				if (event instanceof NodeCollapseEvent && ((NodeCollapseEvent) event).getTreeNode() != null) {
					collapseEvents.forEach(e -> e.accept(event, ((NodeCollapseEvent) event).getTreeNode()));
				}
			}
		}

		public static class OnNodeFilter<T> implements AjaxBehaviorListener, Serializable {
			private static final long serialVersionUID = 1L;
			private final List<Changed<EventObject, TreeNode<T>>> filterEvents;

			public OnNodeFilter(List<Changed<EventObject, TreeNode<T>>> filterEvents) {
				this.filterEvents = filterEvents;
			}

			@Override
			public void processAjaxBehavior(AjaxBehaviorEvent event) throws AbortProcessingException {
				filterEvents.forEach(e -> e.accept(event, null));
			}
		}

		public static class OnNodeDragDrop<T> implements AjaxBehaviorListener, Serializable {
			private static final long serialVersionUID = 1L;
			private final List<Changed<TreeNode<T>[], TreeNode<T>>> dragDropEvents;

			public OnNodeDragDrop(List<Changed<TreeNode<T>[], TreeNode<T>>> dragDropEvents) {
				this.dragDropEvents = dragDropEvents;
			}

			@SuppressWarnings("unchecked")
			@Override
			public void processAjaxBehavior(AjaxBehaviorEvent event) throws AbortProcessingException {
				if (event instanceof TreeDragDropEvent && ((TreeDragDropEvent) event).getDragNode() != null) {
					TreeNode<T> dropNode = ((TreeDragDropEvent) event).getDropNode();
					TreeNode<T> dragNode = ((TreeDragDropEvent) event).getDragNode();
					TreeNode<T>[] dragNodes = ((TreeDragDropEvent) event).getDragNodes();
					if (dragNodes != null && dropNode != null) {
						dragDropEvents.forEach(e -> e.accept(dragNodes, dropNode));
					} else if (dragNode != null && dropNode != null) {
						dragDropEvents.forEach(e -> {
							TreeNode<T>[] dragNodeList = Arrays.asList(dragNode).toArray(s -> new TreeNode[s]);
							e.accept(dragNodeList, dropNode);
						});
					}
				}
			}
		}
	}

	/**
	 * Utility class to simplify the programmatic creation and configuration of
	 * AjaxBehavior. This class replicates the functionality of the declarative
	 * <f:ajax> tag.
	 */
	public final static class Ajax {

		public static AjaxBehavior addListener(UIComponent component, String event, AjaxBehaviorListener listener) {
			return addListener(component, event, "@this", listener);
		}

		public static AjaxBehavior addListener(UIComponent component, String event, String execute, AjaxBehaviorListener listener) {
			return addListener(component, event, execute, null, listener);
		}

		public static AjaxBehavior addListener(UIComponent component, String event, String execute, String render, AjaxBehaviorListener listener) {
			AjaxBehavior behavior = addBehavior(component, event, execute, render);
			behavior.addAjaxBehaviorListener(listener);
			return behavior;
		}

		public static AjaxBehavior addBehavior(UIComponent component, String event) {
			return addBehavior(component, event, "@this");
		}

		public static AjaxBehavior addBehavior(UIComponent component, String event, String execute) {
			return addBehavior(component, event, execute, null);
		}

		public static AjaxBehavior addBehavior(UIComponent component, String event, String execute, String render) {
			if (component instanceof UIComponentBase) {
				AjaxBehavior behavior = createAjaxBehavior(event, execute, render);
				((UIComponentBase) component).addClientBehavior(event, behavior);
				return behavior;
			}
			return null;
		}

		/**
		 * Programmatically creates and configures an AjaxBehavior instance.
		 *
		 * @param event   The client-side event that triggers the request (e.g.,
		 *                "change", "click"). This is not set on the behavior itself,
		 *                but used when attaching it.
		 * @param execute Space-separated list of component IDs/keywords to process on
		 *                the server side (e.g., "@this", "input1 input2").
		 * @param render  Space-separated list of component IDs/keywords to update in
		 *                the DOM after the AJAX response (e.g., "@form", ":id").
		 * @return A configured AjaxBehavior instance.
		 */
		public static AjaxBehavior createAjaxBehavior(String event, String execute, String render) {
			FacesContext context = FacesContext.getCurrentInstance();
			Application app = context.getApplication();
			// 1. Create the behavior instance by ID (AjaxBehavior.BEHAVIOR_ID is the
			// constant "jakarta.faces.behavior.Ajax")
			Behavior behavior = app.createBehavior(AjaxBehavior.BEHAVIOR_ID);
			if (!(behavior instanceof AjaxBehavior)) {
				// Should not happen in a standard JSF environment
				throw new IllegalStateException(
						"Could not create AjaxBehavior with ID: " + AjaxBehavior.BEHAVIOR_ID);
			}
			AjaxBehavior ajaxBehavior = (AjaxBehavior) behavior;
			// 2. Configure the behavior properties using the correct Collection<String>
			// methods.
			// Convert space-separated execute string to Collection<String> for setExecute()
			if (execute != null && !execute.trim().isEmpty()) {
				// Note: split("\\s+") handles multiple spaces correctly
				Collection<String> executeList = Arrays.asList(execute.trim().split("\\s+"));
				ajaxBehavior.setExecute(executeList);
			}
			// Convert space-separated render string to Collection<String> for setRender()
			if (render != null && !render.trim().isEmpty()) {
				Collection<String> renderList = Arrays.asList(render.trim().split("\\s+"));
				ajaxBehavior.setRender(renderList);
			}
			return ajaxBehavior;
		}

		public static class PrimeTreeAjax {

			static final String EXPAND_EVENT = "expand";
			static final String COLLAPSE_EVENT = "collapse";
			static final String SELECT_EVENT = "select";
			static final String CONTEXT_EVENT = "contextMenu";
			static final String UNSELECT_EVENT = "unselect";
			static final String DRAGDROP_EVENT = "dragdrop";
			static final String EVENT = "filter";

			private FacesContext context;

			private org.primefaces.component.tree.Tree pTree;

			public PrimeTreeAjax(FacesContext context, org.primefaces.component.tree.Tree pTree) {
				this.context = context;
				this.pTree = pTree;
			}

			public void addBehavior(String eventName, AjaxBehaviorListener onEvent) {
				addBehavior(onEvent, eventName, "@this");
			}

			public void addBehavior(AjaxBehaviorListener onEvent, String eventName, String executeProcess) {
				addBehavior(onEvent, eventName, executeProcess, "");
			}

			public void addBehavior(AjaxBehaviorListener onEvent, String eventName, String executeProcess, String renderUpdate) {
				addClientBehavior(context, pTree, onEvent, eventName, executeProcess, renderUpdate);
			}

			public static org.primefaces.behavior.ajax.AjaxBehavior addClientBehavior(FacesContext context, UIComponentBase pComponent, AjaxBehaviorListener onEvent, String eventName, String executeProcess, String renderUpdate) {
				if (pComponent.getClientBehaviors().containsKey(eventName)) {
					return null;
				}
				Application app = context.getApplication();
				// 1. Create the behavior instance by ID
				// !!! (AjaxBehavior.BEHAVIOR_ID is the constant
				// "org.primefaces.behavior.ajax.AjaxBehavior")
				org.primefaces.behavior.ajax.AjaxBehavior pAjaxBehavior = (org.primefaces.behavior.ajax.AjaxBehavior) app
						.createBehavior(org.primefaces.behavior.ajax.AjaxBehavior.BEHAVIOR_ID);
				// 2. Configure the behavior properties
				if (executeProcess != null && !executeProcess.trim().isEmpty()) {
					pAjaxBehavior.setProcess(executeProcess);
				}
				if (renderUpdate != null && !renderUpdate.trim().isEmpty()) {
					pAjaxBehavior.setUpdate(renderUpdate);
				}
				pAjaxBehavior.addAjaxBehaviorListener(onEvent);
				pComponent.addClientBehavior(eventName, pAjaxBehavior);
				return pAjaxBehavior;
			}

			/**
			 * Programmatically creates and configures an
			 * {@link org.primefaces.behavior.ajax.AjaxBehavior} instance.
			 *
			 * @param event   The client-side event that triggers the request (e.g.,
			 *                "change", "click"). This is not set on the behavior itself,
			 *                but used when attaching it.
			 * @param execute Space-separated list of component IDs/keywords to process on
			 *                the server side (e.g., "@this", "input1 input2").
			 * @param render  Space-separated list of component IDs/keywords to update in
			 *                the DOM after the AJAX response (e.g., "@form", ":id").
			 * @return A configured {@link org.primefaces.behavior.ajax.AjaxBehavior}
			 *         instance.
			 */
			public static org.primefaces.behavior.ajax.AjaxBehavior addClientBehavior(String execute, String render) {
				FacesContext context = FacesContext.getCurrentInstance();
				Application app = context.getApplication();
				// 1. Create the behavior instance by ID
				// !!! (AjaxBehavior.BEHAVIOR_ID is the constant
				// "org.primefaces.behavior.ajax.AjaxBehavior")
				org.primefaces.behavior.ajax.AjaxBehavior ajaxBehavior = (org.primefaces.behavior.ajax.AjaxBehavior) app
						.createBehavior(org.primefaces.behavior.ajax.AjaxBehavior.BEHAVIOR_ID);
				if (!(ajaxBehavior instanceof org.primefaces.behavior.ajax.AjaxBehavior)) {
					// Should not happen in a standard JSF environment
					throw new IllegalStateException(
							"Could not create AjaxBehavior with ID: " + AjaxBehavior.BEHAVIOR_ID);
				}
				// 2. Configure the behavior properties
				if (execute != null && !execute.trim().isEmpty()) {
					ajaxBehavior.setProcess(execute);
				}
				if (render != null && !render.trim().isEmpty()) {
					ajaxBehavior.setUpdate(render);
				}
				return ajaxBehavior;
			}

			public static org.primefaces.behavior.ajax.AjaxBehavior createAjaxBehavior(String expression, String nodeParamSuffix, Class<?> specificEventType) {
				FacesContext context = FacesContext.getCurrentInstance();
				org.primefaces.behavior.ajax.AjaxBehavior behavior = (org.primefaces.behavior.ajax.AjaxBehavior) context
						.getApplication()
						.createBehavior(org.primefaces.behavior.ajax.AjaxBehavior.BEHAVIOR_ID);
				// create all possible method expressions
				MethodExpression noArgExpr = createVoidMethodExpression(expression, new Class<?>[] {});
				MethodExpression ajaxEventExpr = createVoidMethodExpression(expression,
						new Class<?>[] { AjaxBehaviorEvent.class });
				MethodExpression specificEventExpr = createVoidMethodExpression(expression, specificEventType);
				behavior.addAjaxBehaviorListener(
						new AjaxMethodExpressionListener(noArgExpr, ajaxEventExpr, specificEventExpr));
				return behavior;
			}

			private static MethodExpression createVoidMethodExpression(String expression, Class<?>... parameterTypes) {
				return createMethodExpression(expression, Void.class, parameterTypes);
			}

			private static MethodExpression createMethodExpression(String expression, Class<?> returnType, Class<?>... parameterTypes) {
				FacesContext context = FacesContext.getCurrentInstance();
				return context.getApplication()
						.getExpressionFactory()
						.createMethodExpression(context.getELContext(), expression, returnType, parameterTypes);
			}

			/**
			 * Implements {@link AjaxBehaviorListenerImpl} and stores MethodExpressions for
			 * all signatures (no Arguments, {@link AjaxBehaviorEvent}, specific Event), to
			 * imitate PrimeFaces AjaxBehaviorHandlers.
			 */
			public static class AjaxMethodExpressionListener
					implements AjaxBehaviorListener, PartialStateHolder, Serializable {
				private static final long serialVersionUID = 1L;

				private MethodExpression noArgExpr;
				private MethodExpression ajaxEventExpr;
				private MethodExpression specificEventExpr;
				private boolean isTransient;
				private boolean initialStateMarked;

				public AjaxMethodExpressionListener() {}

				public AjaxMethodExpressionListener(MethodExpression noArgExpr, MethodExpression ajaxEventExpr,
						MethodExpression specificEventExpr) {
					this.noArgExpr = noArgExpr;
					this.ajaxEventExpr = ajaxEventExpr;
					this.specificEventExpr = specificEventExpr;
				}

				@Override
				public void processAjaxBehavior(AjaxBehaviorEvent event) throws AbortProcessingException {
					FacesContext context = FacesContext.getCurrentInstance();
					// PrimeFaces logic first
					if (specificEventExpr != null) {
						specificEventExpr.invoke(context.getELContext(), new Object[] { event });
					} else if (ajaxEventExpr != null) {
						ajaxEventExpr.invoke(context.getELContext(), new Object[] { event });
					} else if (noArgExpr != null) {
						noArgExpr.invoke(context.getELContext(), new Object[] {});
					}
				}

				@Override
				public boolean isTransient() { return isTransient; }

				@Override
				public void restoreState(FacesContext context, Object state) {
					if (state == null) {
						return;
					}
					Object[] values = (Object[]) state;
					this.noArgExpr = (MethodExpression) values[0];
					this.ajaxEventExpr = (MethodExpression) values[1];
					this.specificEventExpr = (MethodExpression) values[2];
				}

				@Override
				public Object saveState(FacesContext context) {
					if (initialStateMarked()) {
						return null;
					}
					return new Object[] { noArgExpr, ajaxEventExpr, specificEventExpr };
				}

				@Override
				public void setTransient(boolean isTransient) { this.isTransient = isTransient; }

				@Override
				public void clearInitialState() { initialStateMarked = false; }

				@Override
				public boolean initialStateMarked() { return initialStateMarked; }

				@Override
				public void markInitialState() { initialStateMarked = true; }
			}

			/**
			 * Wraps a method expression in a AjaxBehaviorListener
			 */
			public static class AjaxMethodExprListener
					implements AjaxBehaviorListener, PartialStateHolder, Serializable {
				private static final long serialVersionUID = 1L;

				private MethodExpression expr;
				private boolean isTransient;
				private boolean initialStateMarked;

				public AjaxMethodExprListener() {}

				public AjaxMethodExprListener(MethodExpression expr) { this.expr = expr; }

				@Override
				public void processAjaxBehavior(AjaxBehaviorEvent event) throws AbortProcessingException {
					expr.invoke(FacesContext.getCurrentInstance().getELContext(), new Object[] { event });
				}

				@Override
				public boolean isTransient() { return isTransient; }

				@Override
				public void restoreState(FacesContext context, Object state) {
					if (state == null) {
						return;
					}
					expr = (MethodExpression) state;
				}

				@Override
				public Object saveState(FacesContext context) {
					if (initialStateMarked()) {
						return null;
					}
					return expr;
				}

				@Override
				public void setTransient(boolean isTransient) { this.isTransient = isTransient; }

				@Override
				public void clearInitialState() { initialStateMarked = false; }

				@Override
				public boolean initialStateMarked() { return initialStateMarked; }

				@Override
				public void markInitialState() { initialStateMarked = true; }
			}
		}
	}

	//
	// Core Behavior - Functional Interfaces (must be Serializable!)
	//

	public interface WrapComponent<T extends UIComponent> {
		void render(FacesContext context, T uiComponent);
	}

	@FunctionalInterface
	public interface Action<T> extends Consumer<T>, Serializable {
		default Action<T> andThen(Action<? super T> after) {
			Objects.requireNonNull(after);
			return (T t) -> {
				accept(t);
				after.accept(t);
			};
		}
	}

	@FunctionalInterface
	public interface Changed<T, U> extends BiConsumer<T, U>, Serializable {
		default Changed<T, U> andThen(Changed<? super T, ? super U> after) {
			Objects.requireNonNull(after);
			return (l, r) -> {
				accept(l, r);
				after.accept(l, r);
			};
		}
	}

	@FunctionalInterface
	public interface Mapped<T, R> extends Function<T, R>, Serializable {
		default <V> Mapped<V, R> compose(Mapped<? super V, ? extends T> before) {
			return (V v) -> apply(Objects.requireNonNull(before).apply(v));
		}

		default <V> Mapped<T, V> andThen(Mapped<? super R, ? extends V> after) {
			return (T t) -> Objects.requireNonNull(after).apply(apply(t));
		}

		static <T> Mapped<T, T> identity() { return t -> t; }
	}

}