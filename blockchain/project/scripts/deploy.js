// scripts/send-ads.js
const fs = require('fs');

async function main() {
    // 1. Читаем файл с 1000 адресов
    const addresses = fs.readFileSync('list.txt', 'utf8').split('\n');

    // 2. Подключаемся к контракту
    const AdNFT = await ethers.getContractFactory("AdNFT");
    const contract = await AdNFT.attach("0x...контракт...");

    // 3. Отправляем каждому
    for (let i = 0; i < addresses.length; i++) {
        console.log(`Отправляю ${i + 1}/${addresses.length}`);
        await contract.sendAd(addresses[i], "https://sale.com");
    }
}

main();