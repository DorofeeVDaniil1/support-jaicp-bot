function formatReportData(jsonData) {
    if (!jsonData.rows || !Array.isArray(jsonData.rows)) {
        return "Неверный формат данных: отсутствует массив rows.";
    }

    var result = '';
    var maxLength = 9000; // Максимальная длина ответа в символах

    jsonData.rows.forEach(function(row, rowIndex) {
        // Проверяем, превышена ли максимальная длина
        if (result.length >= maxLength) {
            result += "\n[Результат обрезан: превышен лимит в 9000 символов]";
            return;
        }

        // Проверяем, есть ли элемент в cells
        if (row.cells && row.cells.length > 0) {
            var cell = row.cells[0]; // Берем только первый элемент

            if (jsonData.title === "Проблемы КП") {
                // Формат для "Проблемы КП"
                var rowData = "Кп-" + (rowIndex + 1) + "\n" +
                                "Адрес: " + cell.addressView + "\n" +
                                "ID Отчета: " + cell.reportId + "\n" +
                                "Перевозчик: " + cell.carrierName + "\n" +
                                "Компания: " + cell.companyName + "\n" +
                                "Тариф: " + cell.tariff + "\n" +
                                "Код Кп: " + cell.containerCode + "\n" +
                                "Наличие акта: " + (cell.hasAct ? "Да" : "Нет") + "\n" +
                                "Количество проблем: " + cell.countProblems;

                if (result.length + rowData.length <= maxLength) {
                    result += rowData + '\n===\n';
                }
            } else {
                // Формат для всех остальных отчетов
                if (!cell.reason || !cell.notRemovedContainers || !cell.addressView || !cell.containerCode || !cell.vehicleNumber || !cell.carrierName) {
                    result += "Некоторые данные отсутствуют.\n";
                } else {
                    var rowData = "Кп-" + (rowIndex + 1) + "\n" +
                                    "Проблемы: " + cell.reason + "\n" +
                                    "Не забрано контейнеров: " + cell.notRemovedContainers + "\n" +
                                    "По адресу: " + cell.addressView + "\n" +
                                    "Код Кп: " + cell.containerCode + "\n" +
                                    "Машина: " + cell.vehicleNumber + "\n" +
                                    "Перевозчик: " + cell.carrierName;

                    if (result.length + rowData.length <= maxLength) {
                        result += rowData + '\n===\n';
                    }
                }
            }
        }
    });

    // Если длина результата превышает лимит, добавляем сообщение об обрезке
    if (result.length < maxLength) {
        result = result.substring(0, maxLength) + "\n[Результат обрезан: превышен лимит в 9000 символов]";
    }

    return result;
}