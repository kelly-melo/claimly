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
- **Procedimento:** código/descrição e valor por item
- **Plano:** número da carteirinha

Se a IA não encontrar um campo, ele retorna `null` (sem inventar informações).

## Regras de validação

1. Todos os campos obrigatórios foram encontrados
2. CPF do paciente confere com o titular/dependente do plano
3. CNPJ do prestador é de clínica/laboratório credenciado
4. Data de emissão dentro de 90 dias
5. Procedimento consta na cobertura do plano
6. Valor da nota confere com o pedido de reembolso
7. Documento legível

**Aprovada** quando todas as regras são atendidas.
**Reprovada** quando há campo ausente, CPF/CNPJ inválido ou divergente, prazo vencido, procedimento fora da cobertura ou imagem ilegível. Casos duvidosos vão para revisão de um analista.

## Fluxo da solução

```
Nota fiscal  →  OpenCV  →  Tesseract  →  IA (LLM)  →  Spring Boot  →  Resultado
  (upload)     (trata a    (OCR: extrai   (interpreta    (valida e     (salva no banco
               imagem)      o texto)       e gera JSON)   aplica regras) e retorna status)
```

## Tecnologias

| Tecnologia | Uso |
| --- | --- |
| Java + Spring Boot | Backend e regras de negócio |
| REST API | Endpoints da aplicação |
| OpenCV | Tratamento da imagem (rotação, contraste, ruído) |
| Tesseract | OCR: transforma imagem em texto |
| IA (LLM via API) | Transforma texto em JSON estruturado |
| PostgreSQL | Armazenamento de documentos e resultados |
| Git / GitHub | Versionamento |

## MVP

- Tela de envio simples e mobile-first, com status e motivo na hora
- Endpoint `POST /reembolsos/validar`
- Pipeline mínimo: OpenCV → Tesseract → IA (JSON)
- Validação em Java (campos obrigatórios, CPF do titular, prazo de 90 dias)
- Resultado salvo no PostgreSQL
- Demo final com 3 casos: nota aprovada, nota fora do prazo e foto ilegível

## API

`POST /reembolsos/validar`: recebe a nota fiscal (JPG, PNG ou PDF, até 10MB) no campo `arquivo` (multipart/form-data) e retorna o status, os motivos e a data da validação.

## Status do projeto

- [x] Definição do problema e da solução
- [x] Estrutura inicial do projeto Spring Boot
- [x] Endpoint de upload
- [x] Persistência no PostgreSQL
- [ ] Tratamento de imagem (OpenCV)
- [ ] OCR (Tesseract)
- [ ] Extração com IA
- [ ] Regras de negócio
- [ ] Tela de envio
- [ ] Testes e deploy

## Como rodar

_Em construção. As instruções serão adicionadas conforme o projeto evoluir._
