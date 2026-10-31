package br.unitins.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.unitins.dto.AgendamentoAulaDTO;
import br.unitins.dto.AgendamentoAulaResponseDTO;
import br.unitins.dto.AlunoDTO;
import br.unitins.dto.AlunoResponseDTO;
import br.unitins.dto.AulaPraticaDTO;
import br.unitins.dto.AulaPraticaResponseDTO;
import br.unitins.dto.AutoescolaDTO;
import br.unitins.dto.AutoescolaResponseDTO;
import br.unitins.dto.CategoriaHabilitacaoDTO;
import br.unitins.dto.CategoriaHabilitacaoResponseDTO;
import br.unitins.dto.DocumentoVeiculoDTO;
import br.unitins.dto.InstrutorDTO;
import br.unitins.dto.InstrutorResponseDTO;
import br.unitins.dto.VeiculoDTO;
import br.unitins.dto.VeiculoResponseDTO;
import br.unitins.model.StatusAgendamento;
import br.unitins.problem.RegraNegocioException;
import br.unitins.repository.AlunoRepository;
import br.unitins.repository.AulaPraticaRepository;
import br.unitins.repository.AutoescolaRepository;
import br.unitins.repository.CategoriaHabilitacaoRepository;
import br.unitins.repository.DocumentoVeiculoRepository;
import br.unitins.repository.InstrutorRepository;
import br.unitins.repository.VeiculoRepository;
import br.unitins.util.TestCleanup;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

@QuarkusTest
public class EntidadesCrudIntegridadeTest {

    @Inject
    AutoescolaService autoescolaService;

    @Inject
    InstrutorService instrutorService;

    @Inject
    VeiculoService veiculoService;

    @Inject
    AlunoService alunoService;

    @Inject
    CategoriaHabilitacaoService categoriaService;

    @Inject
    AulaPraticaService aulaPraticaService;

    @Inject
    AgendamentoAulaService agendamentoService;

    @Inject
    AutoescolaRepository autoescolaRepository;

    @Inject
    InstrutorRepository instrutorRepository;

    @Inject
    VeiculoRepository veiculoRepository;

    @Inject
    DocumentoVeiculoRepository documentoVeiculoRepository;

    @Inject
    AlunoRepository alunoRepository;

    @Inject
    CategoriaHabilitacaoRepository categoriaRepository;

    @Inject
    AulaPraticaRepository aulaPraticaRepository;

    @Inject
    TestCleanup testCleanup;

    private Long idCatA;
    private Long idCatB;

    @BeforeEach
    public void setup() {
        // Garante ausência de agendamentos orfãos que bloqueiam exclusões nos testes.
        // SEGURANÇA: opera no banco autoescola_test (perfil %test),
        // jamais toca o banco de desenvolvimento autoescola.
        // O TestCleanup usa @Transactional próprio, garantindo commit antes do teste iniciar.
        testCleanup.limparAgendamentos();

        CategoriaHabilitacaoResponseDTO catA = categoriaRepository.find("upper(nome)", "A").firstResultOptional()
                .map(CategoriaHabilitacaoResponseDTO::fromEntity)
                .orElseGet(() -> categoriaService.cadastrar(new CategoriaHabilitacaoDTO("A", "Motos")));
        idCatA = catA.id();

        CategoriaHabilitacaoResponseDTO catB = categoriaRepository.find("upper(nome)", "B").firstResultOptional()
                .map(CategoriaHabilitacaoResponseDTO::fromEntity)
                .orElseGet(() -> categoriaService.cadastrar(new CategoriaHabilitacaoDTO("B", "Carros")));
        idCatB = catB.id();
    }

    @Test
    public void testAutoescolaCrudEUnicidadeCnpj() {
        String cnpjUnico = "55.666.777/0001-88";

        AutoescolaDTO dto = new AutoescolaDTO("Autoescola Centro", cnpjUnico, "3211-0000", "centro@autoescola.com", "Rua Centro, 50");
        AutoescolaResponseDTO criado = autoescolaService.cadastrar(dto);
        assertNotNull(criado.id());

        // Rejeitar CNPJ duplicado
        AutoescolaDTO dtoDuplicado = new AutoescolaDTO("Outra", cnpjUnico, "3211-1111", "outra@autoescola.com", "Rua Centro, 52");
        assertThrows(RegraNegocioException.class, () -> autoescolaService.cadastrar(dtoDuplicado));

        // Consulta por ID
        AutoescolaResponseDTO consultado = autoescolaService.buscarPorId(criado.id());
        assertEquals("Autoescola Centro", consultado.nome());

        // Atualização
        AutoescolaDTO dtoAtualizado = new AutoescolaDTO("Autoescola Centro Atualizada", cnpjUnico, "3211-9999", "centro@autoescola.com", "Rua Centro, 50");
        AutoescolaResponseDTO atualizado = autoescolaService.atualizar(criado.id(), dtoAtualizado);
        assertEquals("Autoescola Centro Atualizada", atualizado.nome());

        // Exclusão
        autoescolaService.excluir(criado.id());
        assertThrows(NotFoundException.class, () -> autoescolaService.buscarPorId(criado.id()));
    }

    @Test
    public void testAlunoCrudEUnicidadeCpf() {
        String cpfUnico = "123.456.789-99";

        AlunoDTO dto = new AlunoDTO("Joao da Silva", cpfUnico, "9999-8888", "joao.teste@gmail.com", true);
        AlunoResponseDTO criado = alunoService.cadastrar(dto);
        assertNotNull(criado.id());

        // Rejeitar CPF duplicado
        AlunoDTO dtoDuplicado = new AlunoDTO("Outro Aluno", cpfUnico, "9999-7777", "outro.joao@gmail.com", true);
        assertThrows(RegraNegocioException.class, () -> alunoService.cadastrar(dtoDuplicado));

        // Atualização
        AlunoDTO dtoAtualizado = new AlunoDTO("Joao da Silva Sauro", cpfUnico, "9999-0000", "joao.sauro@gmail.com", false);
        AlunoResponseDTO atualizado = alunoService.atualizar(criado.id(), dtoAtualizado);
        assertEquals("Joao da Silva Sauro", atualizado.nome());
        assertFalse(atualizado.ativo());

        // Exclusão
        alunoService.excluir(criado.id());
        assertThrows(NotFoundException.class, () -> alunoService.buscarPorId(criado.id()));
    }

    @Test
    public void testInstrutorNNComCategoriasESemExclusaoEmCascata() {
        AutoescolaResponseDTO auto = autoescolaService.cadastrar(new AutoescolaDTO(
                "Autoescola Instrutor Teste", "22.333.444/0001-55", "3212-0000", "instrutor.teste@autoescola.com", "Rua B"));

        InstrutorDTO dto = new InstrutorDTO(
                "Instrutor Multi", "333.444.555-66", "REG-MULTI-01", "9876-5432", true,
                auto.id(), List.of(idCatA, idCatB));

        InstrutorResponseDTO criado = instrutorService.cadastrar(dto);
        assertNotNull(criado.id());
        assertEquals(2, criado.categorias().size());

        // Excluir instrutor
        instrutorService.excluir(criado.id());

        // Verificar que as categorias compartilhadas A e B NÃO foram apagadas!
        assertNotNull(categoriaService.buscarPorId(idCatA));
        assertNotNull(categoriaService.buscarPorId(idCatB));

        // Limpeza autoescola
        autoescolaService.excluir(auto.id());
    }

    @Test
    public void testVeiculo1Para1ComDocumentoEUnicidadePlaca() {
        AutoescolaResponseDTO auto = autoescolaService.cadastrar(new AutoescolaDTO(
                "Autoescola Veiculo Teste", "33.444.555/0001-66", "3213-0000", "veiculo.teste@autoescola.com", "Rua C"));

        DocumentoVeiculoDTO docDTO = new DocumentoVeiculoDTO("DOC-12345", LocalDate.of(2028, 6, 30));
        VeiculoDTO veiculoDTO = new VeiculoDTO(
                "UNI-1B23", "Chevrolet", "Onix", 2021, true,
                auto.id(), idCatB, docDTO);

        VeiculoResponseDTO criado = veiculoService.cadastrar(veiculoDTO);
        assertNotNull(criado.id());
        assertNotNull(criado.documento());
        assertEquals("DOC-12345", criado.documento().numero());

        // Rejeitar placa duplicada
        VeiculoDTO veiculoDuplicado = new VeiculoDTO(
                "UNI1B23", "Ford", "Ka", 2020, true,
                auto.id(), idCatB, new DocumentoVeiculoDTO("DOC-OUTRO", LocalDate.of(2028, 6, 30)));
        assertThrows(RegraNegocioException.class, () -> veiculoService.cadastrar(veiculoDuplicado));

        // Atualizar veículo e seu documento
        DocumentoVeiculoDTO docAtualizado = new DocumentoVeiculoDTO("DOC-12345-RENOVADO", LocalDate.of(2029, 6, 30));
        VeiculoDTO veiculoAtualizadoDTO = new VeiculoDTO(
                "UNI-1B23", "Chevrolet", "Onix Plus", 2021, true,
                auto.id(), idCatB, docAtualizado);

        VeiculoResponseDTO atualizado = veiculoService.atualizar(criado.id(), veiculoAtualizadoDTO);
        assertEquals("Onix Plus", atualizado.modelo());
        assertEquals("DOC-12345-RENOVADO", atualizado.documento().numero());

        // Excluir veículo (deve remover seu documento por composição 1:1)
        veiculoService.excluir(criado.id());
        assertThrows(NotFoundException.class, () -> veiculoService.buscarPorId(criado.id()));

        // Limpeza autoescola
        autoescolaService.excluir(auto.id());
    }

    @Test
    public void testImpedirExclusaoFisicaComAgendamentosVinculados() {
        AutoescolaResponseDTO auto = autoescolaService.cadastrar(new AutoescolaDTO(
                "Autoescola Hist Teste", "44.555.666/0001-77", "3214-0000", "hist@autoescola.com", "Rua D"));

        AlunoResponseDTO aluno = alunoService.cadastrar(new AlunoDTO(
                "Aluno Hist", "777.666.555-44", "9111-2222", "aluno.hist@gmail.com", true));

        InstrutorResponseDTO instrutor = instrutorService.cadastrar(new InstrutorDTO(
                "Instrutor Hist", "666.555.444-33", "REG-HIST-99", "9222-3333", true,
                auto.id(), List.of(idCatB)));

        VeiculoResponseDTO veiculo = veiculoService.cadastrar(new VeiculoDTO(
                "HIS-1A22", "Renault", "Kwid", 2022, true,
                auto.id(), idCatB, new DocumentoVeiculoDTO("CRLV-HIST-01", LocalDate.of(2028, 1, 1))));

        AulaPraticaResponseDTO aula = aulaPraticaService.cadastrar(new AulaPraticaDTO(
                "Aula Hist", "Aula Prática Histórica", new BigDecimal("75.00"), idCatB));

        // Cria agendamento vinculado
        AgendamentoAulaDTO agendamentoDTO = new AgendamentoAulaDTO(
                LocalDate.of(2026, 12, 10), LocalTime.of(8, 0), LocalTime.of(9, 0),
                StatusAgendamento.AGENDADO, aluno.id(), instrutor.id(), veiculo.id(), aula.id());
        AgendamentoAulaResponseDTO agendamento = agendamentoService.cadastrar(agendamentoDTO);

        // Tentar excluir o aluno vinculado -> deve ser rejeitado!
        RegraNegocioException exAluno = assertThrows(RegraNegocioException.class, () -> alunoService.excluir(aluno.id()));
        assertTrue(exAluno.getMessage().contains("agendamentos vinculados"));

        // Tentar excluir o instrutor vinculado -> deve ser rejeitado!
        RegraNegocioException exInstrutor = assertThrows(RegraNegocioException.class, () -> instrutorService.excluir(instrutor.id()));
        assertTrue(exInstrutor.getMessage().contains("agendamentos vinculados"));

        // Tentar excluir o veículo vinculado -> deve ser rejeitado!
        RegraNegocioException exVeiculo = assertThrows(RegraNegocioException.class, () -> veiculoService.excluir(veiculo.id()));
        assertTrue(exVeiculo.getMessage().contains("agendamentos vinculados"));

        // Tentar excluir a aula prática vinculada -> deve ser rejeitado!
        RegraNegocioException exAula = assertThrows(RegraNegocioException.class, () -> aulaPraticaService.excluir(aula.id()));
        assertTrue(exAula.getMessage().contains("agendamentos vinculados"));

        // Limpeza: excluir o agendamento primeiro, depois as entidades
        agendamentoService.excluir(agendamento.id());
        alunoService.excluir(aluno.id());
        instrutorService.excluir(instrutor.id());
        veiculoService.excluir(veiculo.id());
        aulaPraticaService.excluir(aula.id());
        autoescolaService.excluir(auto.id());
    }

    @Test
    public void testErroParaIdInexistente() {
        assertThrows(NotFoundException.class, () -> autoescolaService.buscarPorId(999999L));
        assertThrows(NotFoundException.class, () -> alunoService.buscarPorId(999999L));
        assertThrows(NotFoundException.class, () -> instrutorService.buscarPorId(999999L));
        assertThrows(NotFoundException.class, () -> veiculoService.buscarPorId(999999L));
        assertThrows(NotFoundException.class, () -> agendamentoService.buscarPorId(999999L));
    }
}
