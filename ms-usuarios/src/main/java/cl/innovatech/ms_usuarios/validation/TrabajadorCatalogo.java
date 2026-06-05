package cl.innovatech.ms_usuarios.validation;

import java.util.List;
import java.util.Set;

public final class TrabajadorCatalogo {

	public static final int HORAS_SEMANALES_MIN = 20;
	public static final int HORAS_SEMANALES_MAX = 45;

	private static final Set<String> ROLES_PERMITIDOS = Set.of(
		"Analista Financiero TI",
		"Arquitecto de Software",
		"Backend Lead",
		"Business Analyst",
		"Cloud Architect",
		"DBA",
		"Desarrolladora Backend",
		"Desarrolladora Frontend",
		"Desarrolladora Full Stack",
		"Desarrollador Backend",
		"Desarrollador Frontend",
		"Desarrollador Full Stack",
		"Desarrollador Mobile",
		"DevOps",
		"DevOps Engineer",
		"Diseñadora de Producto",
		"Ingeniero de Datos",
		"Product Owner",
		"Project Manager",
		"QA Automation",
		"QA Lead",
		"QA Manual",
		"Scrum Master",
		"Security Engineer",
		"SRE",
		"Tech Lead",
		"Technical Writer",
		"UX Researcher",
		"UX/UI Designer"
	);

	private TrabajadorCatalogo() {
	}

	public static List<String> rolesPermitidos() {
		return ROLES_PERMITIDOS.stream().sorted().toList();
	}

	public static boolean esRolValido(String rol) {
		return rol != null && ROLES_PERMITIDOS.contains(rol.trim());
	}

	public static boolean esCapacidadHorasValida(Integer capacidadHoras) {
		return capacidadHoras != null
			&& capacidadHoras >= HORAS_SEMANALES_MIN
			&& capacidadHoras <= HORAS_SEMANALES_MAX;
	}
}
