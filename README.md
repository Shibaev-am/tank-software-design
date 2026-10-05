# tank-software-design

Запуск: `./gradlew run` (в IDE: Gradle → application → run).

Сборка и тесты: `./gradlew build`.

Нужен JDK 11. На Apple Silicon для исходной версии libGDX нужен JDK x64 и Rosetta 2.

Управление: стрелки или WASD. Переход на соседнюю клетку занимает 0,4 секунды.
Деревья блокируют движение; танк поворачивается даже при заблокированном пути.

- `model` — игровое состояние: `GameModel`, `GameObject`, `Tank`, `Tree`, `Field`, `Direction`.
- `graphics` — отрисовка и ресурсы: `GameRenderer`, `FieldView`, `TankView`, `TreeView`, `SpriteView`.
- `input` — обработка клавиш (`KeyboardHandler`) и привязки движения (`TankControls`).
- `TankGame` — жизненный цикл приложения.
- `GameDesktopLauncher` — запуск окна.

Модель и обработчик ввода не зависят от графики. Для каждого класса в `model` и `input`
есть unit-тесты, которые запускаются без окна и OpenGL.

`KeyboardHandler.bindHeld(action, keys)` выполняет действие при удержании клавиши,
`bindPressed(action, keys)` — один раз при нажатии. Новое действие регистрируется отдельно:
например, когда появится стрельба, `keyboard.bindPressed(tank::shoot, SPACE)`.
