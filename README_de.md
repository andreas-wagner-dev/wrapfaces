# **🎭 WrapFaces**
WrapFaces ist die objektorientierte **Wrap**per Lösung für Jakarta Server **Faces** (JSF). Das leichgewichtige Framework überträgt die **objektorientierte Disziplin** von Desktop-Frameworks (z.B. Swing/SWT) auf das Web,  was einen "UI of Objects" Ansatz ermöglicht.

**💥 Wenn Du es mit Objektorientierung ernst meinst und Web liebst?** 
* 👉 Dann ist WrapFaces genau das 👍 Richtige für 😎 Dich!

## **🔑 KEY FACTS** *DIE 4 ABSOLUTEN GEBOTE*

WrapFaces ist das **Manifest** gegen anämische Datencontainer und die **technische Kapitulation** vor Markup. Es erzwingt die Rückkehr zu sauberer Software-Architektur durch die Einhaltung dieser vier fundamentalen, **kompromisslosen** Prinzipien:

1. **🚫 KEIN MARKUP-BOILERPLATE:**  
   * Die gesamte UI-Struktur und -Logik wird **ausschließlich** in typsicherem Java-Code definiert. Das XHTML dient lediglich als inaktiver, leerer Container.  
   * **Resultat:** 100 % Refaktorierbarkeit, Compile-Time-Validierung und Eliminierung des XML-Boilerplates.  
2. **🧠 ECHTER HEAP STATE:**  
   * Der View-Zustand (State) lebt als **langlebiges Java-Objekt** im JVM Heap (View-Scoped). Die Statelessness des HTTP-Protokolls wird transparent und vollständig abstrahiert.  
   * **Resultat:** Desktop-Anwendungs-Entwicklungserfahrung im Web; Fokus auf OO-Logik, nicht auf Protokolldetails.  
3. **🛡️ MODEL-AUTONOMIE (SRP-Erzwingung):**  
   * Das Domain-Model (Value Object) übernimmt die volle Verantwortung für seine Darstellung durch die Methode Model::displayFrom(). Dies erzwingt das **Single Responsibility Principle (SRP)**.  
   * **Resultat:** Schluss mit anämischen Daten-Modellen. Die Darstellung gehört zum dargestellten Objekt.  
4. **✅ IMMUTABILITY-BINDING:**  
   * Das Framework verhindert schädliche Setter-Aufrufe durch die UI-Bindung. Stattdessen wird über den map()-Mechanismus eine **neue, unveränderliche Instanz** des Domain-Models aus den UI-Werten erzeugt.  
   * **Resultat:** Garantierte Datenintegrität und Reduktion von Seiteneffekten.

## **🛑 KEINE KOMPROMISSE: „UI of Objects“ als architektonische Dogmatik**

WrapFaces ist mehr als ein Framework – es ist ein **Manifest** für “UI of Objects“ gegen anämische Datencontainer und undichte Abstraktionen in der Web-UI  - ein puristischer Ansatz gegen fundamentalen Mängel des Daten- und Markup-zentrierten MVC durch konsequente, typsichere Abstraktionen. 

🎯 **Ziel:** 100 %-tige Integrität der Objektorientierung in der Webentwicklung mit dem “UI of Objects“ Ansatz.

**„UI of Objects“ ist eine Design-Philosophie, die sich konsequent an den Prinzipien der Objektorientierung orientiert**.

Anstatt die Benutzeroberfläche als eine Ansammlung von Masken, Formularen und Befehlen zu betrachten, wird sie als ein System von interagierenden, realen Objekten verstanden. Der Benutzer agiert nicht mit einer Benutzeroberfläche, die Daten manipuliert, sondern er agiert direkt mit den Objekten, die er im System vorfindet. 

**Grundprinzipien der „UI of Objects“**

* **Objekt im Zentrum:** Die Benutzeroberfläche ist um die Objekte des Systems herum organisiert (z. B. Kunde, Produkt, Bestellung) und nicht um die Aktionen, die man mit ihnen ausführen kann. Der Benutzer wählt zuerst ein Objekt aus und entscheidet dann, was er damit machen möchte.  
* **Enge Kopplung zwischen Domain und UI:** Im Gegensatz zur strengen Trennung im klassischen MVC wird die UI als natürliche Eigenschaft der Domain-Objekte betrachtet. Ein Objekt weiß, wie es sich selbst darstellen soll. Dies führt zu einer hohen Kohäsion und einer starken Kapselung.  
* **Verhalten statt nur Daten:** Domain-Objekte sind keine einfachen Datentransportobjekte (DTOs). Sie kapseln sowohl Daten als auch das Verhalten, das zur Manipulation der Daten notwendig ist. Das „Tell, Don't Ask“-Prinzip wird konsequent angewendet: Die UI-Komponente fordert das Domain-Objekt auf, etwas zu tun, anstatt seine Daten abzufragen und von außen zu manipulieren.  
* **Komposition von Objekten:** Komplexe Benutzeroberflächen entstehen durch die Komposition kleinerer, in sich geschlossener UI-Objekte. Eine ProductList besteht aus mehreren ProductCard-Objekten, die jeweils ihr eigenes Domain-Objekt repräsentieren. 

**Vorteile des Konzepts**

* **Intuitive Bedienung:** Das UI spiegelt die Denkweise des Benutzers wider, der in Objekten und nicht in Aufgaben denkt. Das macht die Bedienung intuitiver.  
* **Hohe Wiederverwendbarkeit:** Die UI-Komponenten sind eng mit ihrem jeweiligen Domain-Objekt verbunden. Das macht sie modular und leichter wiederzuverwenden.  
* **Bessere Wartbarkeit:** Die Kapselung von Daten und Verhalten innerhalb der Objekte sorgt dafür, dass Änderungen lokalisiert bleiben.  
* **Alignierung von UX und OOP:** Das Konzept bringt User Experience (UX) und die Prinzipien der objektorientierten Programmierung (OOP) in Einklang, da beide auf dem Gedanken von Objekten und deren Beziehungen basieren.   
* Das Domain-Objekt ist **autonom** und trägt die Verantwortung für seine Darstellung.  
* K**eine Trennung von Belangen**, keine Controller die Model-Interna kennen - keine feste Kopplung.  
* Die Architektur wird zurück in den **JVM Heap** verlagert, wo sie durch Typsicherheit und echten Zustand geschützt ist.

## **🧠 DIE WAHRHEIT: Die Mängel des Traditionellen MVC**

WrapFaces wurde entwickelt, um die Prinzipien der Objektorientierung (OOP) und des Domain-Driven Design (DDD) in der UI-Schicht konsequent durchzusetzen. Es korrigiert die inhärenten Schwächen des traditionellen, Daten- und Markup-zentrierten MVC-Musters:

| Prinzip | Traditionelles MVC  | UI of Objects mit WrapFaces -> Lösung |
| :---- | :---- | :---- |
| **Kapselung** | Controller verletzen die Kapselung, indem sie interne Model-Daten manipulieren. | Das Value Object erzeugt sich neu. Keine schädlichen Setter-Bindungen durch UI-Elemente. |
| **Model-Rolle** | Das Model degeneriert zu einem anämischen Datencontainer ohne Verhalten. Es ist eine Datenklasse, keine Domain-Entität. | Das Model ist autonom: Ein Value Object mit eingebettetem Verhalten und der Fähigkeit, sich selbst anzuzeigen (displayFrom()). |
| **Controller** | Der Controller agiert als Schaltzentrale, kennt Model-Interna und verletzt das Prinzip **"Tell, Don't Ask"** sowie das Single Responsibility Principle (SRP). | Die Logik ist direkt in typsichere Lambda-Handler integriert. Die Controller-Rolle wird auf minimales Routing reduziert. Die Komponente entscheidet selbst. |
| **Zustand (State)** | Der Entwickler muss den Zustand manuell in der HttpSession verwalten und HTTP-Details behandeln. | Webseiten sind zustandsbehaftete Java-Objekte im JVM Heap. Die HTTP-Protokolldetails werden transparent abstrahiert. |
| **Autonomie** | Die Anzeige- und Manipulationslogik ist über drei Schichten verteilt. Geringe Kohäsion. | Das Domänenobjekt bündelt Daten, Verhalten und Darstellung. Maximale Kohäsion. |

## **🛠️ DIE WERKZEUGE DES PURISTEN**

WrapFaces bietet die notwendigen Werkzeuge, um eine saubere Architektur zu erzwingen:

* 💥 **Markup-Zerstörung:** 100 % der UI-Logik und des Komponentenbaums in Java. Das XHTML-Markup dient lediglich als einfacher, unkritischer Container.  
* ⚡ **Echter Zustand:** Komponenten leben als Java-Objekte im Heap. Das Framework übernimmt das State-Handling.  
* 🧩 **Reine Komposition:** Der UI-Baum wird typsicher in Java definiert. Jede Komponente ist ein gekapseltes Domänenobjekt.  
* 🔒 **Type-Safe Binding:** Das Model-Konzept bindet UI-Werte verzögert (late-binding) an neue, unveränderliche Domain-Model-Instanzen zurück.  
* 🛠️ **Sofort-Aktion:** Aktionen (Klicks, Änderungen, etc.) werden direkt an Java Lambda-Ausdrücke gebunden (.onAction(e -> ...)).

### **WrapFaces: MVC mit Kapselung**

WrapFaces - erzwingt das MVC-Muster aus einer strikt objektorientierten Perspektive – eine klare Abgrenzung von traditionellen Schichten-Architekturen mit vertikalen Layern und tehnischer Verantwortung.

| Rolle | WrapFaces / OOP-Ansatz | Traditionelles JSF / Markup-Ansatz |
| :---- | :---- | :---- |
| **Model** | Das reine Value Object. Es kapselt seine Darstellung (displayFrom()). | Meist eine Managed-Bean, die über Setter-Bindungen die Kapselung verletzt. |
| **View** | Die in Java erzeugte, dynamische Komponenten-Hierarchie. | Die .xhtml-Datei, statisch und schwer zu abstrahieren. |
| **Controller** | Das Verhalten ist direkt in die Komponenten oder die Mapping-Logik integriert. | Die zentrale, überladene Managed Bean, die zu viele Verantwortlichkeiten trägt. |

## **🚀 DER CODE: Typsicherheit in Aktion**

* 🚫 Höre auf, dich mit XML herumzuschlagen und deine Anwendung um die DTOs herumzubauen.
* 🔎 Der Fokus liegt auf typsicherem Java-Code.

**📥 Installation**  
Die Standard-Maven- oder Gradle-Abhängigkeit wird benötigt:  
Maven Dependency
```xml
<dependency>  
    <groupId>org.wrapfaces</groupId>  
    <artifactId>wrapfaces-core</artifactId>  
    <version>[AKTUELLSTE VERSION]</version>  
</dependency>
```
### **Die Anbindung: Der Controller**

Der **Controller** als ```LoginView``` ist der minimale Kontaktpunkt zur JSF-Welt. Er dient nur dazu, den von WrapFaces erzeugten Komponentenbaum zu halten und bereitzustellen. **Geschäftslogik ist hier verboten.**
```java
// Beispiel: Die LoginView (Ihre JSF-Managed-Bean)

@Named @ViewScoped  
public class LoginView {

    private HtmlForm form;

    @PostConstruct  
    public void init() {  
        // 1. Erstellung des Domain-Models  
        User user = new User("admin", new Credential("secret123"));  
        // 2. Das Model erzeugt die UI  
        form = user.displayFrom();   
    }  
      
    // JSF benötigt Getter/Setter für das Binding  
    public HtmlForm getForm() { return form; }  
    public void setForm(HtmlForm form) { this.form = form; }  
}
```
Im zugehörigen XHTML:
```xml
<h:form binding="#{loginView.form}"/>
```
wird nur eine Zeile benötigt.

### **Die Autonome Komponente (Innerhalb der User-Klasse)**

Das Model übernimmt die Verantwortung für seine Darstellung und das Mapping der UI-Werte in eine neue, unveränderliche Instanz:
```java
// UI-Erstellung (innerhalb der User-Klasse)  
public Form displayFrom() {  
      
    PanelGrid<User> userGrid = new PanelGrid<User>("userGrid")  
        // addRow erzeugt Label und Input  
        .addRow(new Label("lblUser", "User:"), new Text("txtUser", this.name))  
        .addRow(new Label("lblSecret", "Secret:"), credential.displayInput())  // ← Composition!  
        // KRITISCH: Definiert, WIE die UI-Werte ein NEUES User-Objekt erzeugen  
        .map(User::new); 

    PanelGroup<?> buttonGroup = new PanelGroup<>("btnGrp",  
        new Button("btnSubmit", "Senden").onAction(e -> {  
            // Abruf des NEUEN MODELS aus den UI-Werten  
            User updatedUser = userGrid.model();   
            System.out.println("Submitted. Updated User: " + updatedUser.toString());  
        })  
    );  
      
    // Die finale Form, gebunden an #{loginController.form}  
    return new Form("loginForm", userGrid, buttonGroup);  
}
```
## **⚙️ IMPLEMENTIERUNGSLEITFADEN: Die Basisklasse als Gatekeeper**

WrapFaces erzwingt Disziplin durch die klare Trennung von technischer Vererbung (JSF) und anwendungsspezifischer Logik (Hooks). Der Wrapper nutzt das Decorator Pattern, um die Logik chirurgisch zu injizieren.

**Zweck des Decorators:** Die Wrapper-Klasse umschließt die native JSF-Komponente und dient als typsicherer Decorator, der klare, überschreibbare Einstiegspunkte (Hooks) in den Rendering-Prozess bietet.

Der **Vertrag:** ```WrapComponent<T>```  
* Definiert den Vertrag zur Übertragung des ```transienten``` **Zustands** auf die unterliegende JSF-Komponente vor dem Rendering.  
* Die Architektur: Die abstrakte Basisklasse (```*Wrap```)  
* Die Basisklasse erbt technisch von der JSF-Komponente, nutzt aber **Delegation** und **Komposition** für die gesamte Anwendungslogik.
* Der zentrale Kontrollpunkt ist in der ```encodeBegin():``` Methode, before das JSF das Rendering ausgeführt.  

```java 
// Die notwendige Vererbung von der JSF-Komponente 
public abstract class LabelWrap extends HtmlOutputText implements WrapComponent<HtmlOutputText>, Serializable {  
    // ... Zustand in transienten Feldern ...  
      
    @Override  
    public void encodeBegin(FacesContext context) throws IOException {  
        // 1. initialize Hook: Überträgt den Java-Zustand auf die JSF-Komponente  
        initialize(context, this);

        // 2. Sichtbarkeits-Check (wird durch initialize gesteuert)  
        if (!this.isRendered()) { return; }   
          
        // 3. head Hook: Ressourcen-Injektion  
        head(context, context.getViewRoot());  
          
        // 4. render Hook: Attribut-Setzung (Letzte Chance)  
        render(context, this);  
          
        // 5. Delegation an das JSF Rendering  
        super.encodeBegin(context);  
    }  
}
```
Die Konkrete Komponente: Überschreiben der Hooks  
Die Logik liegt in den Hooks, die in der konkreten Implementierung oder den Basisklassen überschrieben werden können:

| Hook-Methode | Anwendungsfall (Echte Kontrolle) | Beispiel |
| :---- | :---- | :---- |
| initialize(...) | **Dynamische Sichtbarkeit:** Verhindert das Rendern der gesamten Komponente, wenn eine Bedingung fehlschlägt. | if (!isUserAdmin()) uiComponent.setRendered(false); |
| render(...) | **Attribut-Dominanz:** Setzt dynamische Attribute direkt vor dem Renderer-Aufruf. | uiComponent.getPassThroughAttributes().put("role", "presentation"); |

🤝 **MITWIRKEN:** Beteiligung ist willkommen. Jeder Beitrag muss jedoch den puristischen, objektorientierten Standards des Frameworks genügen.

📄 **Lizenz:** Dieses Projekt steht unter der MIT-Lizenz.

📬 **Support:** Fragen, Anregungen oder Probleme? Melden Sie sich.
