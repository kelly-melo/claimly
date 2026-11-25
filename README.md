# Claimly

> Validação inteligente de notas fiscais para reembolso médico com OCR e IA.

Projeto do semestre da Residência Tecnológica (Avanade + CESAR School), Mentoria 2026.2.

**Autora:** Kelly Priscilla de Siqueira Melo, ADS Regular 2026.2

---

## O problema

Para pedir reembolso médico, o beneficiário fotografa a nota fiscal da consulta ou exame e envia pelo aplicativo. Hoje, um analista confere manualmente cada nota (nome, CPF, valor, procedimento e CNPJ do prestador). Isso gera:

- fila de análise e atraso no pagamento;
- retrabalho quando a nota vem ilegível ou incompleta;
- falta de retorno imediato para o usuário.

**Caso motivador:** um usuário envia a nota de um exame de imagem, mas o app só avisa 3 dias depois que o CNPJ do prestador estava ilegível, obrigando-o a pedir uma segunda via à clínica e reiniciar o processo.

## A solução

O Claimly recebe a foto ou PDF da nota fiscal, extrai as informações com OCR e IA, aplica as regras de negócio da operadora e devolve na hora o resultado (**APROVADO** ou **REPROVADO**) com o motivo em linguagem simples.

## Usuários

| Perfil | Papel |
| --- | --- |
| Beneficiário | Envia a nota e quer saber rápido se o reembolso foi aceito |
| Analista de reembolsos | Recebe a nota já validada, com dados extraídos e motivos de pendência |
| Clínicas e laboratórios | Emitem as notas enviadas pelos beneficiários |
| Gestão da operadora | Acompanha volume, taxa de aprovação automática e motivos de recusa |

## Documento processado

Nota fiscal de serviço (consulta/exame), em foto (JPG/PNG) ou PDF.

**Evolução futura:** guia de solicitação médica, comprovante de pagamento e carteirinha do plano.

## Dados extraídos

- **Nota:** número, data de emissão, valor total, descrição do serviço
- **Paciente:** nome e CPF
- **Prestador:** razão social e CNPJ
- **Procedimento:** código e descrição
- **Plano:** número da carteirinha

Se a IA não encontrar um campo, ele retorna `null` (sem inventar informações).

## Fluxo da solução

```
Nota fiscal  →  OpenCV  →  Tesseract  →  IA (LLM)  →  Spring Boot  →  Resultado
  (upload)     (trata a    (OCR: extrai   (interpreta    (valida e     (salva no banco
               imagem)      o texto)       e gera JSON)   aplica regras) e retorna status)
```

1. **Upload:** a tela envia o arquivo para `POST /reembolsos/validar`.
2. **OpenCV:** converte para escala de cinza, amplia, remove ruído e binariza a imagem.
3. **Tesseract:** extrai o texto da imagem tratada (idioma português).
4. **IA (Groq):** recebe o texto e devolve um JSON com os campos da nota.
5. **Regras:** o Spring Boot valida os dados e decide o status.
6. **Resultado:** é salvo no PostgreSQL e devolvido para a tela com os motivos.

## Regras de validação

**Implementadas:**

1. Todos os campos obrigatórios foram encontrados (número da nota, data, valor, CPF, CNPJ e procedimento)
2. CPF do paciente é válido (dígitos verificadores)
3. CNPJ do prestador é válido (dígitos verificadores)
4. Data de emissão dentro de 90 dias (e não está no futuro)
5. Valor total maior que zero
6. Documento legível (se o OCR não extrai texto suficiente, a nota é reprovada)

**Previstas (próximos passos):**

- CPF confere com o titular/dependente cadastrado no plano
- CNPJ do prestador é de clínica/laboratório credenciado
- Procedimento consta na cobertura do plano
- Valor da nota confere com o valor informado no pedido de reembolso
- Fila de revisão manual para casos duvidosos (o status `REVISAO_MANUAL` já existe)

**Resultado:** a nota é **APROVADA** quando nenhuma regra falha. Caso contrário, é **REPROVADA** e a resposta traz o motivo em linguagem simples.

## Tecnologias

| Tecnologia | Uso |
| --- | --- |
| Java 21 + Spring Boot 4 | Backend e regras de negócio |
| REST API | Endpoint de validação |
| OpenCV | Tratamento da imagem |
| Tesseract (Tess4J) | OCR: transforma imagem em texto |
| Apache PDFBox | Converte PDF em imagem |
| Groq API (`openai/gpt-oss-120b`) | Transforma o texto do OCR em JSON estruturado |
| PostgreSQL | Armazenamento dos resultados |
| Docker Compose | Opção para subir o banco |
| HTML, CSS e JavaScript | Tela de envio (mobile-first, tema claro e escuro) |
| JUnit | Testes das regras de validação |
| Git / GitHub | Versionamento |

## Como rodar

### Pré-requisitos

- Java 21
- Tesseract OCR com o idioma português (`por.traineddata` na pasta `tessdata`)
- PostgreSQL 16 **ou** Docker
- Chave da API Groq (gratuita, sem cartão): https://console.groq.com/keys

### 1. Baixar o projeto

```bash
git clone https://github.com/kelly-melo/claimly.git
cd claimly
```

### 2. Subir o banco de dados

**Opção A: Docker**

```bash
docker compose up -d
```

**Opção B: PostgreSQL instalado**

Crie o usuário e o banco que o projeto espera:

```sql
CREATE USER claimly WITH PASSWORD 'claimly';
CREATE DATABASE claimly OWNER claimly;
```

### 3. Configurar as variáveis de ambiente

A chave da API **nunca** fica no código: ela é lida de uma variável de ambiente.

Windows (PowerShell):

```powershell
$env:GROQ_API_KEY="sua-chave-groq"
$env:TESSDATA_PREFIX="C:\Program Files\Tesseract-OCR\tessdata"
```

Linux/macOS:

```bash
export GROQ_API_KEY="sua-chave-groq"
export TESSDATA_PREFIX="/caminho/para/tessdata"
```

Opcional: `GROQ_MODEL` troca o modelo de IA (padrão: `openai/gpt-oss-120b`).

### 4. Executar

Windows:

```powershell
.\mvnw spring-boot:run
```

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Quando aparecer `Started ClaimlyApplication`, abra http://localhost:8080.

### 5. Testar pela API (opcional)

```bash
curl -F "arquivo=@nota.png" http://localhost:8080/reembolsos/validar
```

### Testes automatizados

```bash
./mvnw test
```

O teste `ClaimlyApplicationTests` sobe o contexto completo e precisa do PostgreSQL rodando.

> Use apenas notas **fictícias** nos testes. O texto extraído é enviado para a API de IA.

## API

`POST /reembolsos/validar`

- **Entrada:** arquivo JPG, PNG ou PDF (até 10 MB) no campo `arquivo` (`multipart/form-data`)
- **Saída:** `id`, `status` (`APROVADO`, `REPROVADO` ou `REVISAO_MANUAL`), `motivos` e `criadoEm`

## Demonstração

Foram usadas 3 notas fictícias:

| Nota | Resultado esperado |
| --- | --- |
| Nota válida, emitida dentro de 90 dias | **APROVADO** |
| Nota emitida há mais de 90 dias | **REPROVADO** (prazo vencido) |
| Foto borrada, torta e cortada | **REPROVADO** (documento ilegível) |

## Decisões técnicas

- **Pipeline em etapas:** cada etapa tem um papel claro (preparar, ler, interpretar, validar e persistir), o que facilita testar e trocar cada peça.
- **OpenCV antes do OCR:** melhora a qualidade da leitura em fotos de celular.
- **IA só para interpretar:** a IA transforma texto em JSON, mas **as regras de negócio ficam em Java**, onde são previsíveis e testáveis.
- **Sem inventar dados:** o prompt manda a IA devolver `null` quando não encontra um campo, e o `null` reprova a nota.
- **`BigDecimal` e `LocalDate`:** valor monetário e datas com tipos adequados.
- **Provedor de IA trocável:** começou com a API Gemini, mas o nível gratuito não estava disponível para o projeto no Google. Foi trocada pela Groq alterando apenas o cliente HTTP. O restante do pipeline não mudou.
- **Segredos fora do código:** chave de API e caminho do Tesseract vêm de variáveis de ambiente.

## Status do projeto

- [x] Definição do problema e da solução
- [x] Estrutura inicial do projeto Spring Boot
- [x] Endpoint de upload
- [x] Tratamento de imagem (OpenCV)
- [x] OCR (Tesseract)
- [x] Extração com IA (Groq)
- [x] Regras de negócio (campos obrigatórios, CPF, CNPJ, prazo de 90 dias e valor)
- [x] Persistência no PostgreSQL
- [x] Tela de envio (mobile-first, tema claro e escuro)
- [x] Testes unitários das regras de validação
- [ ] Regras que dependem do banco (CNPJ credenciado e cobertura do procedimento)
- [ ] Revisão manual pelo analista
- [ ] Deploy
