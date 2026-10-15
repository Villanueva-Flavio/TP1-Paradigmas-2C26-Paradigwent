# Paradigwent

![Paradigwent](https://github.com/user-attachments/assets/628d61ba-e12a-48ac-815e-01c1b9b83e0b)

## Universidad de Buenos Aires, Facultad de Ingeniería

- **Materia:** Paradigmas de Programación
- **Cátedra:** Essaya
- **Trabajo práctico:** TP1
- **Cuatrimestre:** 2C2026

## Integrantes

- **Flavio Villanueva:** 108014
- **Emanuel Peña:** 108065

## Docentes

- **Alberto Alejandro Carmona**
- **Diego Essaya**

## Descripción

Paradigwent es un juego de cartas inspirado en Gwent, desarrollado en Java y
JavaFX. El juego enfrenta a un jugador contra una inteligencia artificial en
un tablero dividido en líneas de ataque.

Cada jugador utiliza cartas de distintas razas y tipos. Las cartas pueden ser
minions, hechizos o cartas de clima. El objetivo es utilizar las cartas y sus
efectos para obtener una mayor cantidad de poder en el tablero y ganar la
ronda.

El proyecto se gestiona con Maven y utiliza:

- Java 21.
- JavaFX 21.0.1 para la interfaz gráfica.
- Gson 2.10.1 para cargar cartas y efectos desde JSON.
- JUnit 5 para las pruebas automatizadas.

## Presentaciones

- Flavio Villanueva: https://www.youtube.com/watch?v=JU3p1aqJBso
- Emanuel Peña: https://www.youtube.com/watch?v=8Vpf08s2UkM

## Funcionalidades principales

- Juego de un jugador contra la CPU.
- Selección de facción o raza.
- Tablero con líneas de ataque y zonas diferenciadas.
- Cartas de tipo minion, spell y weather.
- Efectos de destruir, modificar poder, revivir, robar cartas y duplicar
  líneas.
- Efectos climáticos como lluvia, niebla, nieve, tormenta y clima despejado.
- Selección de objetivos para efectos que lo requieren.
- Iluminación de líneas y cartas seleccionables.
- Indicadores visuales para cartas buffeadas o nerfeadas.
- Música de menú, música de partida, música climática y efectos de sonido.
- Artwork, offsets, escalas y configuración visual de cartas desde
  `src/main/resources/json/cards.json`.
- Menú principal, colección de cartas y configuración durante la partida.

## Requisitos

Para compilar y ejecutar el proyecto se necesita:

- JDK 21 o superior.
- Maven 3.8 o superior.
- Un sistema con soporte gráfico para ejecutar JavaFX.

Se puede verificar la instalación con:

```bash
java -version
mvn -version
```

## Compilación

Desde la carpeta raíz del proyecto, ejecutar:

```bash
mvn clean compile
```

Para compilar y ejecutar todas las pruebas:

```bash
mvn clean test
```

La compilación copia automáticamente los recursos gráficos, sonidos, videos,
fuentes, hojas de estilo y archivos JSON necesarios para la aplicación.

## Ejecución

Para iniciar la aplicación JavaFX con Maven:

```bash
mvn javafx:run
```

La clase principal configurada por el proyecto es:

```text
com.fiuba.paradigwent.view.MainView
```

También existe una clase de entrada en:

```text
src/main/java/com/fiuba/paradigwent/Main.java
```

Si se ejecuta desde un IDE, se debe utilizar una configuración de aplicación
con esa clase principal y un JDK 21.

## Organización del proyecto

El código fuente se encuentra en `src/main/java` y está organizado por
responsabilidades:

```text
src/
├── main/
│   ├── java/com/fiuba/paradigwent/
│   │   ├── model/
│   │   │   ├── ai/
│   │   │   ├── board/
│   │   │   ├── card/
│   │   │   ├── effect/
│   │   │   ├── enums/
│   │   │   ├── game/
│   │   │   ├── player/
│   │   │   └── round/
│   │   ├── controller/
│   │   └── view/
│   └── resources/
│       ├── audio/
│       ├── css/
│       ├── fonts/
│       ├── images/
│       ├── json/
│       └── videos/
└── test/
    └── java/
```

### Modelo

El modelo contiene el estado y las reglas del juego:

- `Game`, `Round` y `GameContext` coordinan el estado general.
- `Board`, `PlayerSide` y `AttackLine` representan el tablero.
- `Player`, `AI`, `Deck`, `Hand` y `Discard` representan a los jugadores y sus
  cartas.
- `Card`, `Minion`, `Spell` y `Weather` representan los tipos de carta.
- El paquete `effect` contiene los efectos aplicables a las cartas.

### Controladores

Los controladores coordinan las acciones entre la interfaz y el modelo:

- `GameController` valida y ejecuta las jugadas.
- `BoardController` coordina operaciones relacionadas con el tablero.
- `GameSessionFactory` crea las sesiones de juego y carga la configuración.
- `GameStatePrinter` permite representar el estado del juego.

### Vista

La vista está construida con JavaFX:

- `MainView` administra la aplicación y la navegación principal.
- `GameView` muestra la partida.
- `Board3DView`, `RowView`, `HandView` y otros módulos representan el tablero,
  las líneas y las cartas.
- `MainMenuView` y los módulos de menú muestran las pantallas auxiliares.
- `AudioManager` centraliza la música y los efectos de sonido.

La vista recibe información del modelo para poder renderizar cartas y estados
del tablero, mientras que las acciones principales de juego se coordinan a
través del controlador.

## Efectos y polimorfismo

Los efectos se modelan mediante la interfaz `Effect`. Cada efecto concreto
implementa el mismo contrato, pero define un comportamiento diferente:

```text
Effect
├── DestroyCreatureEffect
├── DuplicateLineEffect
├── ModifyPowerEffect
├── ReviveEffect
├── StealCardEffect
└── WeatherEffect
```

Esto permite que el juego ejecute distintos efectos de forma uniforme. La
creación de los efectos se centraliza en `EffectFactory`, evitando que el
resto del código tenga que conocer todos los detalles de construcción de cada
efecto.

La inteligencia artificial utiliza una estrategia intercambiable mediante
`AIStrategy`, con `RandomAIStrategy` como implementación actual.

## Configuración y recursos

El catálogo de cartas se encuentra en:

```text
src/main/resources/json/cards.json
```

Los efectos y sus configuraciones se encuentran en:

```text
src/main/resources/json/effects.json
```

Los recursos multimedia están organizados de la siguiente manera:

- `images/`: cartas, fondos, tablero y elementos visuales.
- `audio/music/`: música de menú, partida y clima.
- `audio/effects/`: sonidos de acciones del juego.
- `css/`: estilos de la interfaz.
- `fonts/`: fuentes utilizadas por la aplicación.
- `videos/`: video de fondo del menú principal.

## Diagramas UML

El proyecto incluye diagramas PlantUML separados para facilitar su lectura:

- [UML general](uml.puml)
- [UML del modelo](uml-model.puml)
- [UML de controladores](uml-controller.puml)
- [UML de la vista](uml-view.puml)

## Agradecimientos y créditos de assets

Esta sección se completará con los créditos definitivos de los recursos
utilizados en el proyecto.

(A agregar)
1. **Asset 1:** nombre del autor, recurso utilizado y enlace.
2. **Asset 2:** nombre del autor, recurso utilizado y enlace.
3. **Asset 3:** nombre del autor, recurso utilizado y enlace.
4. **Asset 4:** nombre del autor, recurso utilizado y enlace.

## Licencia y uso académico

Este proyecto fue desarrollado con fines académicos para la materia
Paradigmas de Programación de la Facultad de Ingeniería de la Universidad de
Buenos Aires.
