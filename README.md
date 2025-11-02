# 🎮 Tetris Java Game

Et klassisk Tetris-spil udviklet i Java med fokus på gameplay, responsiv styring og moderne features som **hold-system**, **ghost-piece**, **linje-fjernelse**, **level progression** og **hard drop**.

Projektet er bygget fra bunden i Java Swing / 2D Graphics.

---

## 🚀 Funktioner

✅ Klassisk Tetris gameplay   
✅ Ghost-piece, så man kan se hvor brikken lander  
✅ Hold-funktion (gem og byt brikker)  
✅ Hard drop (instant drop med SPACE)  
✅ Linjefjernelse, score og level-system  
✅ Collision-check og rotationer  
✅ UI til score, level, lines, next & hold  
✅ Lyd-effekter og baggrundsmusik  

---

## 🎮 Styring

| Tast | Handling |
|------|---------|
| ⬅️ Venstre pil | Flyt brik til venstre |
| ➡️ Højre pil | Flyt brik til højre |
| ⬇️ Ned pil | Hurtigt fald |
| ⬆️ Pil | Rotér brik |
| **SPACE** | Hard drop |
| **SHIFT / C** | Hold / Swap brik |
| ESC | Luk spillet (hvis implementeret) |

---

## 🧱 Scoring & Levels

| Event | Point |
|-------|------|
| 1 linje | 10 × Level |
| 2–4 linjer | Stigende multiplikator |
| Hver 10. linje | Level up (hurtigere hastighed) |

Jo højere level → jo hurtigere drop speed.

---

## 🛠️ Teknologi

- **Java (17+)**
- **Swing & Java2D Graphics**
- Objekt-orienteret struktur med Mino-klasser
- Lyd via indbyggede Java-audio APIs

Ingen eksterne biblioteker nødvendig ✅

---

## ▶️ Kør spillet

### Kommandolinje
```sh
javac -d bin src/**/*.java
java -cp bin main.Game
