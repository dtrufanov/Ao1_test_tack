# A test task from Ao1 Solution

## Initial Data:

+ Several CSV files. The number of files can be quite large (up to 100,000).
+ The number of rows within each file can reach up to several million.
+ Each file contains 5 columns: 
  + product ID (integer),
  + Name (string),
  + Condition (string),
  + State (string),
  + Price (float).
+ The same product IDs may occur more than once in different CSV files and in the same CSV file.

## Task:

Write a console utility using Java programming language that allows getting a selection of the cheapest 1000 products from the input CSV files, but no more than 20 products with the same ID. Use parallel processing to increase performance.

## Utility Result:

Output CSV file that meets the following criteria:

+ no more than 1000 products sorted by Price from all files;
+ no more than 20 products with the same ID.

# Решение
## Сборка

Сборка осуществляется с помощь Maven:

`mvn clean install`

Программа собирается в исполняемый jar.

## Запуск

`java -jar ./target/ao1-test-1.0-SNAPSHOT.jar <input dir> <output file> [<processors count>]`

### Параметры

+ input dir - директория, в которой находятся входные файлы. Ожидается, что все входные файлы находятся непосредственно в этой директории.
+ output file - файл, куда будет записан результат.
+ processors count - число потоков, которое будет использовано для асинхронной обработки
  + если не указать, будет использовано число доступных процессоров минус 1
  + если меньше 0, реальной обработки не будет, только чтение файлов - использовалось для оценки времени на чтение
  + если равно 0, то используется последовательный алгоритм, без применения многопоточности
  + если больше 0, то 1 поток будет читать данные, а указанное число - обрабатывать
  
### Логи

Логи записываются в файл log.txt. Ошибки и предупреждения выводятся также на консоль.

