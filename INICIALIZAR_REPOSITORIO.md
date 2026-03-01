# Instrucciones para Inicializar el Repositorio

Este proyecto está listo para ser subido a un nuevo repositorio Git.

## Pasos para inicializar:

1. **Navega al directorio del proyecto:**
   ```bash
   cd ms-archetype-springboot-webflux-java-pragma
   ```

2. **Inicializa el repositorio Git:**
   ```bash
   git init
   ```

3. **Agrega todos los archivos:**
   ```bash
   git add .
   ```

4. **Realiza el primer commit:**
   ```bash
   git commit -m "Initial commit: Spring Boot WebFlux Integration Layer Archetype"
   ```

5. **Conecta con tu repositorio remoto:**
   ```bash
   git remote add origin <URL_DE_TU_REPOSITORIO>
   ```

6. **Sube los cambios:**
   ```bash
   git branch -M main
   git push -u origin main
   ```

## Notas:

- Este proyecto NO contiene historial de Git previo
- Todos los archivos de configuración de IDE (.idea, .vscode) han sido eliminados
- Los archivos de build (.gradle, build/) han sido limpiados
- Se ha incluido un .gitignore apropiado para proyectos Spring Boot

## Siguiente paso:

Una vez subido al repositorio, puedes eliminar este archivo:
```bash
git rm INICIALIZAR_REPOSITORIO.md
git commit -m "docs: remove initialization instructions"
git push
```
