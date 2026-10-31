package br.unitins.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

import org.junit.jupiter.api.Test;

import br.unitins.dto.AlunoDTO;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
public class AlunoResourceTest {

    @Test
    public void testCadastrarEBuscarPorId() {
        AlunoDTO dto = new AlunoDTO(
                "Lucas Aluno Rest",
                "111.222.333-01",
                "(63) 98888-1111",
                "lucas.rest@email.com",
                true
        );

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(dto)
            .when()
                .post("/alunos")
            .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("nome", equalTo("Lucas Aluno Rest"))
                .body("cpf", equalTo("11122233301"))
            .extract()
                .path("id");

        given()
            .when()
                .get("/alunos/" + id)
            .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("nome", equalTo("Lucas Aluno Rest"));
    }

    @Test
    public void testCadastrarComDadosInvalidosRetorna400() {
        AlunoDTO dtoInvalido = new AlunoDTO(
                "A",
                "cpf_invalido",
                "",
                "email_invalido",
                true
        );

        given()
                .contentType(ContentType.JSON)
                .body(dtoInvalido)
            .when()
                .post("/alunos")
            .then()
                .statusCode(400);
    }

    @Test
    public void testCadastrarCpfDuplicadoRetorna400() {
        String cpf = "222.333.444-02";

        AlunoDTO dto1 = new AlunoDTO(
                "Aluno Original",
                cpf,
                "9888-2222",
                "original.aluno@email.com",
                true
        );

        given()
                .contentType(ContentType.JSON)
                .body(dto1)
            .when()
                .post("/alunos")
            .then()
                .statusCode(201);

        AlunoDTO dto2 = new AlunoDTO(
                "Aluno Duplicado",
                cpf,
                "9888-2223",
                "duplicado.aluno@email.com",
                true
        );

        given()
                .contentType(ContentType.JSON)
                .body(dto2)
            .when()
                .post("/alunos")
            .then()
                .statusCode(400);
    }

    @Test
    public void testListarTodosEBuscarAtivos() {
        given()
            .when()
                .get("/alunos")
            .then()
                .statusCode(200);

        given()
            .when()
                .get("/alunos/ativos?ativo=true")
            .then()
                .statusCode(200);
    }

    @Test
    public void testBuscarPorIdInexistenteRetorna404() {
        given()
            .when()
                .get("/alunos/999999")
            .then()
                .statusCode(404);
    }

    @Test
    public void testBuscarPorCpf() {
        String cpfFormatado = "333.444.555-03";
        String cpfNormalizado = "33344455503";

        AlunoDTO dto = new AlunoDTO(
                "Aluno Busca CPF",
                cpfFormatado,
                "9888-3333",
                "busca.cpf@email.com",
                true
        );

        given()
                .contentType(ContentType.JSON)
                .body(dto)
            .when()
                .post("/alunos")
            .then()
                .statusCode(201);

        given()
            .when()
                .get("/alunos/cpf/" + cpfNormalizado)
            .then()
                .statusCode(200)
                .body("cpf", equalTo(cpfNormalizado))
                .body("nome", equalTo("Aluno Busca CPF"));
    }

    @Test
    public void testAtualizarEExcluir() {
        AlunoDTO dto = new AlunoDTO(
                "Aluno Atualizar Excluir",
                "444.555.666-04",
                "9888-4444",
                "atualizar.aluno@email.com",
                true
        );

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(dto)
            .when()
                .post("/alunos")
            .then()
                .statusCode(201)
            .extract()
                .path("id");

        AlunoDTO dtoAtualizado = new AlunoDTO(
                "Aluno Atualizado Com Sucesso",
                "444.555.666-04",
                "9888-4999",
                "atualizado.aluno@email.com",
                false
        );

        given()
                .contentType(ContentType.JSON)
                .body(dtoAtualizado)
            .when()
                .put("/alunos/" + id)
            .then()
                .statusCode(200)
                .body("nome", equalTo("Aluno Atualizado Com Sucesso"))
                .body("ativo", equalTo(false));

        given()
            .when()
                .delete("/alunos/" + id)
            .then()
                .statusCode(204);

        given()
            .when()
                .get("/alunos/" + id)
            .then()
                .statusCode(404);
    }
}
