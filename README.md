# Open-Encoder API
##### Backend
[![Build Status](https://travis-ci.org/joemccann/dillinger.svg?branch=master)](https://travis-ci.org/joemccann/dillinger)

## Sobre a aplicação
O Open-Encoder é uma aplicação criada com o intuito de ser uma plataforma aberta de encoding de vídeos. 

A aplicação tem a funcionalidade de converter arquivos de vídeo de um formato **não compatível** com os padrões da web para um formato que seja **compatível** com os padrões da web. 
Dessa forma, ao converter o seu vídeo, você receberá um link fornecido pela API,  então você pode tocar o seu vídeo em **qualquer dispositivo** que possua um navegador Web. 

A proposta da aplicação foi feita pela empresa Sambatech.
#### Link da API REST: https://api.open-encoder.caiovitor.com:8080/

### Features
* ### Aplicação front-end:
    * [Link do repositório no GitHub](https://github.com/caiovitord/Frontend-Open-Encoder)
    * [Link do website do front-end da aplicação](https://caiovitor.com/open-encoder/)
* ##### Implementações de segurança, como a comunicação em **rede criptografada**, utilizando protocolo HTTPS tanto no front-end como no back-end;
* ##### Utilização das ferramentas de especificações da **OpenAPI** juntamente com **Swagger-UI**;
  * [Link da documentação interativa Swagger-UI da API ](https://api.open-encoder.caiovitor.com:8080/swagger-ui.html)
* ##### Conteinerização da aplicação por meio de **Docker** tanto no front-end como no back-end;
* ##### Testes de integração automatizados. Ideal para rapidamente criar o seu próprio *deploy* da aplicação

# Maneiras de utilizar a API
### 1 - Utilizando a aplicação front-end
Para testar a API da maneira mais intuitiva e descomplicada, acesse o link: https://caiovitor.com/open-encoder.
Somente na aplicação do front-end você envia o seu vídeo e em seguida recebe um **link único** com acesso ao **Video Player do prórprio site**.

Confira um link de exemplo. Veja: [Link único de vídeo](https://caiovitor.com/open-encoder/#/video/1567041559233)
### 2 - Utilizando a documentação interativa Swagger-UI 
Para acessar a documentação interativa, use o link: [Documentação interativa Swagger-UI](https://api.open-encoder.caiovitor.com:8080/swagger-ui.html)
### 3 - Utilizando outro software para realizar requisições HTTP

O **Postman** é o software recomendado para poder testar a API do Open-Encoder.
Se você deseja rapidamente poder testar todos os endpoints com o postman, faça a importação da **Coleção de Requisições**  da API Open-Encoder. 

A coleção de requisições pode ser importada no Postman utilizando o arquivo 
**Open-Encoder-API-REST.postman_collection.json**.
O arquivo se encontra na **pasta raiz do projeto.**


# Roteiro de utilizaçao da API
Primeiramente, é importante ler lista completa de endpoints.
Neste roteiro, será indicado o número do endpoint a ser utilizado.
Na lista completa de endpoints é descrito com detalhes cada parâmetro necessário para cada endpoint, e como ele deve ser enviado.

#### 1.1 - Envie o seu arquivo 
Para criar o arquivo de **input** do encoding de vídeo, utilize o endpoint número **3 - POST api/v1/files**

Após o arquio ser enviado, este endpoint retorna no corpo da requisição, o nome do arquivo que se encontra no bucket S3. 

Este nome de arquivo será utilizado nos próximos passos do consumo da API, então salve o nome do arquivo fornecido pela requisição.
#
#### 1.2 - Solicite o encoding
Com o nome do arquivo obtido no passo anterior. Crie uma requisição para o endpoint número **4 - POST api/v1/encodings**.

**Essa requisição irá solicitar que o servidor inicie o processo de encoding.**

Para iniciar o processo de encoding, escolha a sua qualidade de vídeo (parâmetro encodingQuality).
 - Baixa: Valor do parâmetro **encodingQuality**: "LOW"
 - Média: Valor do parâmetro **encodingQuality**: "MEDIUM"
 - Alta: Valor do parâmetro **encodingQuality**: "HIGH"

Observe as especificações de cada qualidade de encoding:
 - HIGH: Tamanho do vídeo: 1024px Bitrate: 1500 Kbps
 - MEDIUM: Tamanho do vídeo: 640px Bitrate: 750 Kbps
 - LOW: Tamanho do vídeo: 384px Bitrate: 375 Kbps

O corpo (body) da sua requisição deve ser um JSON contendo os dois parâmetros (**fileName** e **encodingQuality**).

Observe que é necessário que a requisição seja feita com **Content-Type: application/json**.

Ao realizar a requisição do endpoint  **4 - POST api/v1/encodings** você obterá uma resposta do tipo:
```sh
{
    "encodingId": "062f728c-a731-4c41-844e-fdb903abaa33",
    ....
}
```
###### **O valor de encodingId será utilizado como parâmetro de todos os passos subsequentes, então salve este dado**.

Nesse ponto, o seu encoding já está sendo iniciado pela API bitmovin. Ele ficará alguns minutos na fila, e depois será devidamente processado.

#
#### 1.3 - Verifique o status do seu encoding
Com o valor de **encodingId**, execute o endpoint **6 - GET api/v1/encodings/{encodingId}/status**. O parâmetro encodingId deve ser enviado pela URL como no exemplo abaixo:

GET api/v1/encodings/062f728c-a731-4c41-844e-fdb903abaa33/status
O retorno deste endpoint te informará se o seu encoding está em um dos seguintes status:
- Na fila do encoding (QUEUED)
- Sendo processado (PROCESSING)
- Finalizado o processo de encoding (FINISHED)
##### Você deve aguardar o retorno do status ser igual a "FINISHED" e só depois seguir para o próximo passo.
#
#### 1.4 - Solicite a criação do arquivo manifest
Após esperar alguns minutos para o encoding ser feito por completo. Você deve realizar a requisição em: 
**POST /api/v1/encodings/{encodingId}/manifest**

O retorno desta requisição já é o link do arquivo manifest HLS para acessar o seu vídeo.

**Atenção nesta parte!**
Você pode ver o seu vídeo diretamente do navegador simplesmente colando o link na barra de endereço. Entretanto, **alguns navegadores podem optar equivocadamente por fazer download do arquivo, ao invés de toca-lo**. 

Pelo que testei, o navegador **Microsoft Edge** toca o vídeo com o link direto do arquivo manifest HLS, por outro lado, o navegador **Google Chrome** e **Firefox** fazem o download do arquivo ao invés de tocar.

Caso o seu navegador esteja fazendo o download do arquivo ao invés de toca-lo, você pode utilizar o **Player da Bitmovin**:
Entre no [player de testes da Bitmovin](https://bitmovin.com/demos/stream-test), **no menu lateral direito, em "Stream" marque a opção HLS**, insira o seu link gerado e clique em **Load settings** para tocar o seu vídeo.

Exemplo de link de vídeo HLS: https://open-encoder-output.s3.amazonaws.com/1567041559233/manifest.m3u8

#### No [front-end](https://caiovitor.com/open-encoder) da aplicação, existe um player HLS pronto para tocar os seus vídeos convertidos. Não deixe de conferir!
#
#### 1.5 - (Opcional) Obtenha o link novamente

Após já ter requisitado a criação do manifest no passo anterior, se você quiser obter o link do seu vídeo novamente, basta utilizar o endpoint 
**8 - GET /api/v1/encodings/{encodingId}/link**

# Lista completa de endpoints
### 1 - GET   /

**Descrição**: Endpoint para verificar se o servidor está respondendo requisições.

**Parâmetros**: Nenhum

**Retorna**: 
Status code 200.

Exatamente esta mensagem no **corpo de resposta**: "Hello I`m here";

### 2 - GET   /api/v1

**Descrição**: Endpoint para verificar se o servidor está respondendo requisições. Ele também informa quando a instância do servidor foi ligada.

**Parâmetros**: Nenhum

**Retorna**: 
Status code 200.

Mensagem em texto com a data.
Exemplo: "Hello! I'm awake! I've been working since Thu Aug 29 00:59:13 UTC 2019";
    
### 3 - POST   api/v1/files

**Descrição**: Endpoint para enviar um arquivo que servirá de **input** para o processo de encoding.

**Parâmetros**: **Atenção!** O arquivo deve ser enviado necessariamente por meio de **form-data**, com o encoding **multipart/form-data.**.


**Retorna**: 
Status code 200.


**Corpo da resposta**: Nome do arquivo de vídeo no bucket AWS. (Esse nome de arquivo será utilizado como INPUT em endpoints seguintes)

### 4 - POST   api/v1/encodings

**Descrição**: Endpoint para solicitar que o processo de encoding comece. Ele realiza, de maneira transparente, uma requisição para a API **Bitmovin**. É necessário fornecer o nome do arquivo de input enviado no endpoint 3 (acima).


**Parâmetros**: Os parâmetros 1 e 2 descritos abaixo devem ser enviados por meio de **Content-Type: application/json**.


Parâmetro 1 - **fileName** - Tipo: String - Descrição: Nome do arquivo de input no bucket AWS (fornecido pelo endpoint 3)


Parâmetro 2 - **encodingQuality** - Tipo: String (ENUMERADO) 


Descrição: Os valores aceitos para o parâmetro de qualidade do encoding são uma das 3 Strings: LOW MEDIUM HIGH

**Retorna**: 
Status code 404, caso não exista o arquivo de input  

Status code 200, caso exista o arquivo de input  

**Corpo da resposta**: JSON com o objeto VideoEncodingRequest
Exemplo: 
```sh
{
    "encodingId": "062f728c-a731-4c41-844e-fdb903abaa33",
    "inputFileName": "1567039917278JUNIT_test_sample.mkv",
    "outputPath": "1567039926742",
    "audioStreamId": "262b814e-a891-409e-835a-a22d603c6de7",
    "fmp4AudioMuxinId": "ad58f717-be3f-4d9b-8118-3e4de7c8751e",
    "streamVideoId": "4ccfb479-8def-4d75-8df3-962c21c0e520",
    "videoMuxinId": "54286792-18fd-453b-bd22-7ff614caf148",
    "encodingQuality": "LOW"
}
```

### 5 - GET   api/v1/encodings/{encodingId}

**Descrição**: Endpoint para obter o objeto VideoEncodingRequest. Não necessariamente precisa ser utilizada no processo de solicitação de encoding, pois o endpoint 4 já retorna o objeto VideoEncodingRequest.  


**Parâmetros**: Os parametros 1 e 2 descritos abaixo devem ser enviados por meio de **Content-Type: application/json**.  

Parâmetro 1 - **fileName** - Tipo: String - Descrição: Nome do arquivo input no bucket AWS (fornecido pelo endpoint 3)  

Parâmetro 2 - **encodingQuality** - Tipo: String (ENUMERADO). Descrição: Os valores aceitos para o parâmetro de qualidade do encoding são uma das 3 Strings: LOW MEDIUM HIGH  

Parâmetro 3 - **encodingId** deve ser enviado por meio da URL da requisição  



**Retorna**: 
Status code 404, caso não exista o objeto VideoEncodingRequest com este id.  

Status code 200, caso exista o objeto VideoEncodingRequest  


**Corpo da resposta**: JSON com o objeto VideoEncodingRequest
Exemplo: 
```sh
{
    "encodingId": "062f728c-a731-4c41-844e-fdb903abaa33",
    "inputFileName": "1567039917278JUNIT_test_sample.mkv",
    "outputPath": "1567039926742",
    "audioStreamId": "262b814e-a891-409e-835a-a22d603c6de7",
    "fmp4AudioMuxinId": "ad58f717-be3f-4d9b-8118-3e4de7c8751e",
    "streamVideoId": "4ccfb479-8def-4d75-8df3-962c21c0e520",
    "videoMuxinId": "54286792-18fd-453b-bd22-7ff614caf148",
    "encodingQuality": "LOW"
}
```

### 6 - GET   api/v1/encodings/{encodingId}/status

**Descrição**: Endpoint para obter o **status** da requisição de vídeo  

**Parâmetros**:  

Parâmetro 1 - **encodingId** deve ser enviado por meio da URL da requisição


**Retorna**: 
Status code 404, caso não exista o objeto VideoEncodingRequest com este id.  

Status code 200, caso exista o objeto encoding  

**Corpo da resposta**: JSON com o objeto Status da requisição de vídeo
Exemplo: 
```sh
{
    "status": "FINISHED",
    "eta": 0,
    "progress": 100,
    "subtasks": []
    "messages": []
}
```

### 7 - POST   /api/v1/encodings/{encodingId}/manifest

**Descrição**: Endpoint para solicitar a criação do arquivo **manifest** do vídeo. Esse arquivo é o output do encoding, o resultado.

**Para que a criação do manifest não gere um erro na API Bitmovin, é necessário esperar o status do encoding ser** **FINISHED**. Verifique o status do encoding com o endpoint número 6  

**Este método deve ser utilizado após a finalização do encoding**  

**Parâmetros**:  

Parâmetro 1 - **encodingId** deve ser enviado por meio da URL da requisição  


**Retorna**:  

Status code 404, caso não exista o objeto VideoEncodingRequest com este id.  

Status code 200, caso exista o objeto encoding  


**Corpo da resposta**: Um link de output do encoding finalizado:  

Exemplo: https://open-encoder-output.s3.amazonaws.com/1567041559233/manifest.m3u8  

### 8 - GET   /api/v1/encodings/{encodingId}/link

**Descrição**: Endpoint para obter o link do arquivo de output do encoding finalizado.  

**Parâmetros**: 
Parâmetro 1 - **encodingId** deve ser enviado por meio da URL da requisição  

**Retorna**: 
Status code 404, caso não exista o objeto VideoEncodingRequest com este id.  

Status code 200, caso exista o objeto encoding 

**Corpo da resposta**: Um link de output do encoding finalizado:  


Exemplo: https://open-encoder-output.s3.amazonaws.com/1567041559233/manifest.m3u8

### 9 - DELETE   /api/v1/encodings/{encodingId}

**Descrição**: Endpoint para deletar o objeto de requisição de encoding vídeo da base de dados.  

**Parâmetros**:  

Parâmetro 1 - **encodingId** deve ser enviado por meio da URL da requisição

**Retorna**:  

Status code 404, caso não exista o objeto VideoEncodingRequest com este id.  

Status code 204 (No content), caso exista o objeto encoding e ele foi deletado  

**Corpo da resposta vazio**


# Como executar a sua própria instância da API 

## 1 - Configurar o ambiente
Você pode criar a sua própria instância do back-end do Open-Encoder e compilar a sua própria versão do software. Para isso, é necessário ter o ambiente configurado com o **Java JDK 8** e **Maven**.

Siga as instruções contidas nos tutoriais para configurar o seu sistema.
- [Como instalar Java JDK no Windows ou Ubuntu:](https://bgasparotto.com/pt/java/instalar-o-java-8-no-windows-e-ubuntu/m)
- [Como instalar o Maven no Windows](https://dicasdejava.com.br/como-instalar-o-maven-no-windows/)
- [Como instalar o Maven no Ubuntu](https://medium.com/@andgomes/instalando-o-apache-maven-no-ubuntu-de4a95a5975a)

Atenção na configuração das variáveis de ambiente para que o Maven funcione corretamente.
## 2 - Clonar o projeto
Com o ambiente de desenvolvimento configurado, faça o download deste projeto.
Abra a pasta raiz do projeto com a sua IDE preferida.
##### Recomendo que seja utilizado o ambiente IntelliJ IDEA

## 3 - Configurar as APIs
O primeiro passo para configurar a sua aplicação é inserir as suas **chaves de acesso** aos serviços necessários.
#####
O Open Encoder funciona com a seguinte infraestrutura:
- 1 **bucket S3** para armazenar os arquivos de vídeo enviados pelos usuários (bucket de input)
- 1 **bucket S3** para armazenar os arquivos já convertidos para o formato web (bucket de output)
- 1 acesso a API de Encoding **Bitmovin** (Existe uma versão gratuita para testes, acesse: [Bitmovin](https://bitmovin.com/))
- 1 chave de acesso de **usuário IAM** da AWS com permissão de leitura e escrita aos buckets

Atenção, é necessário que o bucket de output esteja com **permissão de acesso público**. O acesso deve ser público pois qualquer usuário não autenticado deveria poder acessar o vídeo após o encoding.

Siga os passos abaixo para configurar o projeto


Navegue pelo projeto até a classe **AppConfiguration**, contida em src\main\java\Configuration


Observe o código-fonte:
```sh
public class AppConfiguration {

    //Bucket name configuration
    public static final String AWS_INPUT_BUCKET_NAME = "nome-do-seu-bucket-input";
    public static final String AWS_OUTPUT_BUCKET_NAME = "nome-do-seu-bucket-output";

    //AWS API KEY configuration
    //Chaves de exemplo, não funcionais
    public static final String AWS_ACCESS_KEY = "AKIAX4GJZQVTXHFJZVOI";
    public static final String AWS_SECRET = "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";

    //Bitmovin API KEY configuraton
    //Chave de exemplo, não funcional
    public static final String BITMOVIN_API_KEY = "91e8346c-a81c-4f09-b5cc-3b246f80e87d";

}
```
Agora basta fazer o seguinte:
- #####  1 - Altere nomes dos buckets de entrada e saída
- #####  2 - Altere as chaves da API de armazenamento
- #####  3 - Altere a chave da API Bitmovin

Simples assim! O seu projeto está pronto para ser compilado e testado.
## 4 - Compilar
Com todas as APIs configuradas, você pode compilar o seu código. Utilize o seguinte comando Maven na pasta raiz do projeto:
```sh
mvn clean install
```
O primeiro build pode demorar alguns minutos, até que o Maven faça download de todas as bibliotecas.

#### Com o seu projeto compilado você pode executar os testes de integração automatizados. 
##### Essa é uma forma simples e automática de verificar se o seus buckets, usuário IAM e chaves da API bitmovin estão corretamente configurados.

Para executar os testes, utilize o seguinte comando Maven na pasta raiz do projeto:
```sh
mvn test
```
## 5 - Executar o servidor
### 5.1 - Executar utilizando Docker
O docker é a forma mais prática de criar uma instância do seu servidor. 
O docker cria um *container* para a sua aplicação executar isoladamente, facilitando assim o gerenciamento e o *deploy* da sua aplicação.  

Saiba mais sobre o docker com os seguintes links:
 - [Tutorial oficial de como instalar o Docker no Windows ](https://docs.docker.com/docker-for-windows/install/)
 - [Tutorial oficial de como instalar o Docker no Ubuntu](https://docs.docker.com/install/linux/docker-ce/ubuntu/)
 - [Site oficial do Docker](https://www.docker.com/get-started)
 - [Comandos básicos do Docker](https://medium.com/dockerbr/principais-comandos-docker-f9b02e6944cd)

Agora que você já tem o Docker instalado, vamos compilar a sua aplicação em uma **imagem docker**. 
Em seguida, vamos criar um **container** que executa uma instância dessa imagem compilada.

Para compilar a sua imagem, execute o seguinte comando na pasta raiz no projeto.
```sh
docker build -t my-open-encoder .
```
Atenção! 
 - Existe um **ponto** no final do comando acima.
 - Pode ser necessário executar o comando como administrador.

Com isso, aguarde o docker compilar a sua aplicação e gerar uma imagem.
Após finalizar o **build**, você já pode criar um **container** que executa o servidor.

Para isso, rode o seguinte comando. Observe que você pode optar por alterar a porta de serviço do servidor.
```sh
#Configurando o servidor para executar na porta 8080
docker run -p 8080:8080 my-open-encoder

#Configurando o servidor para executar na porta 8123
docker run -p 8123:8080 my-open-encoder
```
Após rodar o comando acima, o console irá te mostrar o output da aplicação em funcionamento. 
#### Pronto! O seu servidor já está sendo executado veja abaixo como verificar se o servidor está acessível na porta selecionada.
####
Caso você não esteja familiarizado com o docker, não deixe de conferir os links acima para saber como gerenciar as suas aplicações (containers).

#
### 5.2 - Executar do modo tradicional
Para executar do modo tradicional, acesse a pasta */target* que se encontra na raiz do projeto.
Essa pasta só é criada após o projeto ser compilado com  *mvn clean install*.

Verifique a existência do arquivo **Backend-Sambatech-Encoding-1.0.jar**
Execute o seguinte comando na pasta */target*

```sh
java -jar Backend-Sambatech-Encoding-1.0.jar
```
O seu aplicativo deverá começar a executar. O console irá te mostrar o output da aplicação em funcionamento. 

## 6 - Verificar o funcionamento do servidor

Agora que sua aplicação já está rodando, você deve verificar se ela está funcionando.
No seu navegador preferido, acesse o link da aplicação por meio do IP local ou domínio.

Acesse no browser, o link do tipo:
 - http://localhost:8080 (Servidor no mesmo computador)
 - http://192.168.1.16:8080 (Ip local do computador)

Uma mensagem **"Hello, I'm here!"** deve aparecer na tela.

Você também pode verificar a quanto tempo o seu servidor está rodando, basta acessar:
 - http://localhost:8080/api/v1/

Agora você pode desfrutar da sua própria API REST de codificação de vídeos.


[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)


   [dill]: <https://github.com/joemccann/dillinger>
   [git-repo-url]: <https://github.com/joemccann/dillinger.git>
   [john gruber]: <http://daringfireball.net>
   [df1]: <http://daringfireball.net/projects/markdown/>
   [markdown-it]: <https://github.com/markdown-it/markdown-it>
   [Ace Editor]: <http://ace.ajax.org>
   [node.js]: <http://nodejs.org>
   [Twitter Bootstrap]: <http://twitter.github.com/bootstrap/>
   [jQuery]: <http://jquery.com>
   [@tjholowaychuk]: <http://twitter.com/tjholowaychuk>
   [express]: <http://expressjs.com>
   [AngularJS]: <http://angularjs.org>
   [Gulp]: <http://gulpjs.com>

   [PlDb]: <https://github.com/joemccann/dillinger/tree/master/plugins/dropbox/README.md>
   [PlGh]: <https://github.com/joemccann/dillinger/tree/master/plugins/github/README.md>
   [PlGd]: <https://github.com/joemccann/dillinger/tree/master/plugins/googledrive/README.md>
   [PlOd]: <https://github.com/joemccann/dillinger/tree/master/plugins/onedrive/README.md>
   [PlMe]: <https://github.com/joemccann/dillinger/tree/master/plugins/medium/README.md>
   [PlGa]: <https://github.com/RahulHP/dillinger/blob/master/plugins/googleanalytics/README.md>
