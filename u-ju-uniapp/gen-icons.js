const fs = require('fs')
const path = require('path')
const zlib = require('zlib')

function crc32(buf) {
  let c = 0xFFFFFFFF
  for (let i = 0; i < buf.length; i++) {
    c ^= buf[i]
    for (let j = 0; j < 8; j++) c = (c >>> 1) ^ (c & 1 ? 0xEDB88320 : 0)
  }
  return (c ^ 0xFFFFFFFF) >>> 0
}

function createPNG(r, g, b, filename) {
  const dir = path.join(__dirname, 'src', 'static', 'tab')
  if (!fs.existsSync(dir)) fs.mkdirSync(dir, { recursive: true })

  const raw = []
  for (let y = 0; y < 48; y++) {
    raw.push(0)
    for (let x = 0; x < 48; x++) raw.push(r, g, b)
  }
  const compressed = zlib.deflateSync(Buffer.from(raw))

  const width = Buffer.alloc(4); width.writeUInt32BE(48)
  const height = Buffer.alloc(4); height.writeUInt32BE(48)

  const ihdrData = Buffer.concat([Buffer.from('IHDR'), width, height, Buffer.from([8,2,0,0,0])])
  const ihdrCrc = Buffer.alloc(4); ihdrCrc.writeUInt32BE(crc32(ihdrData))
  const idatData = Buffer.concat([Buffer.from('IDAT'), compressed])
  const idatCrc = Buffer.alloc(4); idatCrc.writeUInt32BE(crc32(idatData))

  const png = Buffer.concat([
    Buffer.from([0x89,0x50,0x4E,0x47]),
    Buffer.from([0x00,0x00,0x00,0x0D]), ihdrData, ihdrCrc,
    Buffer.from([0x00,0x00,0x00,compressed.length]), idatData, idatCrc,
    Buffer.from([0x00,0x00,0x00,0x00,0x49,0x45,0x4E,0x44,0xAE,0x42,0x60,0x82])
  ])
  fs.writeFileSync(path.join(dir, filename), png)
}

createPNG(156, 163, 175, 'home.png')
createPNG(79, 70, 229, 'home-active.png')
createPNG(156, 163, 175, 'event.png')
createPNG(79, 70, 229, 'event-active.png')
createPNG(156, 163, 175, 'user.png')
createPNG(79, 70, 229, 'user-active.png')
console.log('Tab icons created!')
