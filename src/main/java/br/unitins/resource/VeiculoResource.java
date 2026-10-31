package br.unitins.resource;

import java.util.List;

import br.unitins.dto.VeiculoDTO;
import br.unitins.dto.VeiculoResponseDTO;
import br.unitins.service.VeiculoService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/veiculos")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class VeiculoResource {

    @Inject
    VeiculoService service;

    @GET
    public List<VeiculoResponseDTO> listarTodos() {
        return service.listarTodos();
    }

    @GET
    @Path("/{id}")
    public VeiculoResponseDTO buscarPorId(@PathParam("id") Long id) {
        return service.buscarPorId(id);
    }

    @GET
    @Path("/placa/{placa}")
    public VeiculoResponseDTO buscarPorPlaca(@PathParam("placa") String placa) {
        return service.buscarPorPlaca(placa);
    }

    @GET
    @Path("/categoria/{idCategoria}")
    public List<VeiculoResponseDTO> buscarPorCategoria(@PathParam("idCategoria") Long idCategoria) {
        return service.buscarPorCategoria(idCategoria);
    }

    @GET
    @Path("/autoescola/{idAutoescola}")
    public List<VeiculoResponseDTO> buscarPorAutoescola(@PathParam("idAutoescola") Long idAutoescola) {
        return service.buscarPorAutoescola(idAutoescola);
    }

    @GET
    @Path("/ativos")
    public List<VeiculoResponseDTO> buscarPorAtivo(@QueryParam("ativo") Boolean ativo) {
        return service.buscarPorAtivo(ativo != null ? ativo : true);
    }

    @POST
    public Response cadastrar(@Valid VeiculoDTO dto) {
        VeiculoResponseDTO resposta = service.cadastrar(dto);
        return Response.status(Response.Status.CREATED).entity(resposta).build();
    }

    @PUT
    @Path("/{id}")
    public VeiculoResponseDTO atualizar(
            @PathParam("id") Long id,
            @Valid VeiculoDTO dto) {
        return service.atualizar(id, dto);
    }

    @DELETE
    @Path("/{id}")
    public Response excluir(@PathParam("id") Long id) {
        service.excluir(id);
        return Response.noContent().build();
    }
}
