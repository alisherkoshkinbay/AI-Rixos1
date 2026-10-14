# Rixos Smart Hotel: қонақүйдің ақылды бөлмелерін басқару жүйесі

Java Swing қолданбасы. 6 бөлме (101, 102, 201, 202, 305, 401), әр бөлмеде 5 ақылды құрылғы:
жарық, кондиционер, перде, теледидар, есік құлпы.

![room](docs/room.png)

## Мүмкіндіктер
- Анимациялы бөлме суреті: жарық, перде, ауа ағыны, теледидар, күн мен ай (сыртқы уақытты өзгертуге болады)
- Сценарийлер: Қош келдіңіз, Түнгі режим, Кино, Таң, Үнемдеу, Check-out
- Undo / Redo, слайдер әрекеттері бір командаға біріктіріледі
- Мәтіндік көмекші: «кондиционер 22», «перде аш», «кино режимі», «undo»
- Энергия есебі: Вт, кВт·сағ, құны (₸) және график
- Әкімші беті: барлық бөлменің күйі, бос бөлмелерді бір батырмамен өшіру
- Оқиғалар журналы

## Паттерндер
| Паттерн | Қайда | Не үшін |
|---|---|---|
| **Bridge** | `bridge/RemoteControl`, `AdvancedRemote` ↔ `Device` (`Light`, `AirConditioner`, `Curtains`, `Television`, `DoorLock`) | Пульт пен құрылғы бір-біріне тәуелсіз дамиды. Жаңа құрылғы қосу үшін пультті өзгертудің қажеті жоқ |
| **Command** | `command/*` | Әр әрекет объект: Undo/Redo, сценарий (`MacroCommand`), слайдер командаларын біріктіру |
| **Observer** | `observer/*`, `BaseDevice.fire()` | Құрылғы өзгергенде `ActivityLogger`, `EnergyMonitor`, бөлме суреті мен карточкалар жаңарады |
| **Facade** | `facade/SmartRoomFacade`, `VoiceAssistant` | UI тек `welcome()`, `movieMode()`, `setLevel()` сияқты қарапайым әдістерді шақырады |
| **Singleton** | `singleton/SmartHomeHub` | Бүкіл қонақүйде жалғыз хаб: бөлмелер және ортақ журнал |



## Құрылымы
```
src/smarthome/
├── bridge/      Device, BaseDevice, құрылғылар, RemoteControl, AdvancedRemote
├── command/     Command, Power*/SetLevel/Action/Macro, CommandInvoker
├── observer/    DeviceListener, ActivityLog(ger), EnergyMonitor, ConsoleLogger
├── model/       Room
├── singleton/   SmartHomeHub
├── facade/      SmartRoomFacade, VoiceAssistant

```
