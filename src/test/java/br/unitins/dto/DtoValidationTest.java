package br.unitins.dto;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class DtoValidationTest {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testAutoescolaDtoValidation() {
        // Válido
        AutoescolaDTO valido = new AutoescolaDTO(
                "Autoescola Educar", "12.345.678/0001-90", "(63) 3215-0000", "contato@educar.com.br", "Av. JK, 100");
        assertTrue(validator.validate(valido).isEmpty());

        // CNPJ inválido e e-mail inválido
        AutoescolaDTO invalido = new AutoescolaDTO(
                "", "123", "", "emailinvalido", "");
        Set<ConstraintViolation<AutoescolaDTO>> violations = validator.validate(invalido);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("nome")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("cnpj")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    public void testInstrutorDtoValidation() {
        // Válido
        InstrutorDTO valido = new InstrutorDTO(
                "Carlos Silva", "123.456.789-00", "REG-12345", "(63) 99999-8888", true, 1L, List.of(1L, 2L));
        assertTrue(validator.validate(valido).isEmpty());

        // CPF inválido e lista de categorias vazia
        InstrutorDTO invalido = new InstrutorDTO(
                "", "123", "", "", true, -1L, List.of());
        Set<ConstraintViolation<InstrutorDTO>> violations = validator.validate(invalido);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("cpf")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("idsCategorias")));
    }

    @Test
    public void testVeiculoDtoValidation() {
        // Válido
        DocumentoVeiculoDTO docValido = new DocumentoVeiculoDTO("CRLV-98765", LocalDate.of(2027, 12, 31));
        VeiculoDTO valido = new VeiculoDTO(
                "ABC1D23", "Volkswagen", "Gol", 2022, true, 1L, 1L, docValido);
        assertTrue(validator.validate(valido).isEmpty());

        // Placa inválida e ano < 1900
        VeiculoDTO invalido = new VeiculoDTO(
                "PLACA_ERRADA", "", "", 1800, true, 1L, 1L, null);
        Set<ConstraintViolation<VeiculoDTO>> violations = validator.validate(invalido);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("placa")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("anoFabricacao")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("documento")));
    }

    @Test
    public void testAlunoDtoValidation() {
        // Válido
        AlunoDTO valido = new AlunoDTO(
                "Maria Fernandes", "111.222.333-44", "(63) 98888-7777", "maria@gmail.com", true);
        assertTrue(validator.validate(valido).isEmpty());

        // E-mail inválido e CPF inválido
        AlunoDTO invalido = new AlunoDTO(
                "A", "cpf_invalido", "", "nao_e_email", true);
        Set<ConstraintViolation<AlunoDTO>> violations = validator.validate(invalido);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("cpf")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    public void testPacoteAulasDtoValidation() {
        // Válido
        ItemPacoteAulaDTO item = new ItemPacoteAulaDTO(1L, 5);
        PacoteAulasDTO valido = new PacoteAulasDTO(
                "Pacote Básico Carro", "10 aulas práticas", new BigDecimal("500.00"), List.of(item));
        assertTrue(validator.validate(valido).isEmpty());

        // Sem itens e preço negativo
        PacoteAulasDTO invalido = new PacoteAulasDTO(
                "", "", new BigDecimal("-10.00"), List.of());
        Set<ConstraintViolation<PacoteAulasDTO>> violations = validator.validate(invalido);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("itens")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("preco")));
    }

    @Test
    public void testAgendamentoAulaDtoValidation() {
        // Válido
        AgendamentoAulaDTO valido = new AgendamentoAulaDTO(
                LocalDate.of(2026, 11, 20),
                LocalTime.of(8, 0),
                LocalTime.of(9, 0),
                null,
                1L, 1L, 1L, 1L);
        assertTrue(validator.validate(valido).isEmpty());

        // Campos nulos
        AgendamentoAulaDTO invalido = new AgendamentoAulaDTO(
                null, null, null, null, null, null, null, null);
        Set<ConstraintViolation<AgendamentoAulaDTO>> violations = validator.validate(invalido);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("dataAula")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("horarioInicio")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("horarioTermino")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("idAluno")));
    }
}
