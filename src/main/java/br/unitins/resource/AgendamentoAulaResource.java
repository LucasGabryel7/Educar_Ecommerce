package br.unitins.resource;

import java.time.LocalDate;
import java.util.List;

import br.unitins.dto.AgendamentoAulaDTO;
import br.unitins.dto.AgendamentoAulaResponseDTO;
import br.unitins.model.StatusAgendamento;
import br.unitins.service.AgendamentoAulaService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/agendamentos-aulas")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AgendamentoAulaResource {

    @Inject
    AgendamentoAulaService service;

    @GET
    public List<AgendamentoAulaResponseDTO> listarTodos() {
        return service.listarTodos();
    }

    @GET
    @Path("/{id}")
    public AgendamentoAulaResponseDTO buscarPorId(@PathParam("id") Long id) {
        return service.buscarPorId(id);
    }

    @GET
    @Path("/aluno/{idAluno}")
    public List<AgendamentoAulaResponseDTO> buscarPorAluno(@PathParam("idAluno") Long idAluno) {
        return service.buscarPorAluno(idAluno);
    }

    @GET
    @Path("/instrutor/{idInstrutor}")
    public List<AgendamentoAulaResponseDTO> buscarPorInstrutor(@PathParam("idInstrutor") Long idInstrutor) {
        return service.buscarPorInstrutor(idInstrutor);
    }

    @GET
    @Path("/veiculo/{idVeiculo}")
    public List<AgendamentoAulaResponseDTO> buscarPorVeiculo(@PathParam("idVeiculo") Long idVeiculo) {
        return service.buscarPorVeiculo(idVeiculo);
    }

    @GET
    @Path("/data/{data}")
    public List<AgendamentoAulaResponseDTO> buscarPorData(@PathParam("data") String data) {
        return service.buscarPorData(LocalDate.parse(data));
    }

    @GET
    @Path("/status/{status}")
    public List<AgendamentoAulaResponseDTO> buscarPorStatus(@PathParam("status") StatusAgendamento status) {
        return service.buscarPorStatus(status);
    }

    @POST
    public Response cadastrar(@Valid AgendamentoAulaDTO dto) {
        AgendamentoAulaResponseDTO resposta = service.cadastrar(dto);
        return Response.status(Response.Status.CREATED).entity(resposta).build();
    }

    @PUT
    @Path("/{id}")
    public AgendamentoAulaResponseDTO atualizar(
            @PathParam("id") Long id,
            @Valid AgendamentoAulaDTO dto) {
        return service.atualizar(id, dto);
    }

    @PATCH
    @Path("/{id}/status")
    public AgendamentoAulaResponseDTO atualizarStatus(
            @PathParam("id") Long id,
            @QueryParam("status") StatusAgendamento status) {
        return service.atualizarStatus(id, status);
    }

    @DELETE
    @Path("/{id}")
    public Response excluir(@PathParam("id") Long id) {
        service.excluir(id);
        return Response.noContent().build();
    }
}
