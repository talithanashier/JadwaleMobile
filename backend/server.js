const fs = require('fs');
const path = require('path');

// Auto-detect whether main.js is located at ./dist/src/main.js or ./dist/main.js
if (fs.existsSync(path.join(__dirname, 'dist', 'src', 'main.js'))) {
  require('./dist/src/main.js');
} else if (fs.existsSync(path.join(__dirname, 'dist', 'main.js'))) {
  require('./dist/main.js');
} else {
  console.error("Error: Cannot find main.js inside dist/ or dist/src/");
}
