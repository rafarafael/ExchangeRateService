# Currency Exchange Rate Service

## Descrição
Este projeto é um serviço que monitora constantemente a taxa de câmbio entre o Real (BRL) e o Dólar (USD). Ele fornece uma API RESTful para obter a taxa mais recente e consultar taxas históricas em um intervalo de datas configurável. O serviço é desenvolvido com Spring Boot e armazena os dados em um banco de dados MySQL.

## Tecnologias Usadas
- **Spring Boot**: Framework para desenvolvimento de APIs REST.
- **MySQL**: Banco de dados para armazenar as taxas de câmbio.
- **H2**: Banco de dados em memória para testes.
- **JUnit** e **Mockito**: Ferramentas de teste.
- **Docker**: Para containerização da aplicação.
- **AWS**: Para deployment da aplicação.

## Estrutura do Projeto
A estrutura do projeto é organizada da seguinte maneira:

plaintext
src/main/java/com/exchangerate/  
├── controller/  
│   └── ExchangeRateController.java  
├── model/  
│   ├── entities/  
│   │   └── ExchangeRate.java  
│   └── repositories/  
│       └── ExchangeRateRepository.java  
├── services/  
│   ├── ExchangeRateService.java  
│   └── ExchangeRateApiClient.java  
├── config/  
│   └── HttpClientConfig.java  
└── ExchangeRateServiceApplication.java

## Endpoints da API

- **GET /exchange/latest**: Retorna a taxa mais recente de BRL para USD.
- **GET /exchange/historical?startDate={startDate}&endDate={endDate}**: Retorna as taxas históricas no intervalo de datas especificado.
- **POST /exchange/fetch**: Busca e salva a taxa de câmbio atual.

## Configuração do Fetch Interval

O intervalo de verificação da taxa de câmbio pode ser configurado no arquivo `application.properties`:

properties
fetch.interval=900000 # em milissegundos (15 minutos)

## Testes

Os testes foram implementados para garantir a funcionalidade do serviço. Eles incluem:

- **ExchangeRateControllerTest**: Testa o funcionamento do controlador de taxas de câmbio.
- **ExchangeRateRepositoryTest**: Testa as operações de banco de dados no repositório de taxas de câmbio.
- **ExchangeRateServiceTest**: Verifica a lógica de negócios no serviço de taxas de câmbio.
- **ExchangeRateApiClientTest**: Testa a interação com o cliente da API externa para buscar as taxas de câmbio.
- **ExchangeRateServiceApplicationTest**: Testes de integração para validar a inicialização e o funcionamento do aplicativo.
- **HttpClientConfigTest**: Verifica a configuração do cliente HTTP utilizado para chamadas externas.

## Passo a Passo para Iniciar a Aplicação com Docker

1. Acesse o diretório do projeto:

   cd /caminho/para/seu/projeto

2. Compile o projeto (opcional, mas recomendado):

   mvn clean package

3. Inicie a aplicação com Docker:

   docker-compose up

4. Acesse a aplicação:

   Abra o navegador e vá para http://localhost:8080 (ou a porta configurada no docker-compose.yml).

5. Para parar a aplicação:

   Pressione CTRL+C no terminal onde o Docker está rodando ou execute:

   docker-compose down

