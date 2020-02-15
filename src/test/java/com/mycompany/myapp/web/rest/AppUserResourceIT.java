package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.JhipsterSampleApplicationApp;
import com.mycompany.myapp.domain.AppUser;
import com.mycompany.myapp.repository.AppUserRepository;
import com.mycompany.myapp.repository.search.AppUserSearchRepository;
import com.mycompany.myapp.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.mycompany.myapp.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link AppUserResource} REST controller.
 */
@SpringBootTest(classes = JhipsterSampleApplicationApp.class)
public class AppUserResourceIT {

    private static final String DEFAULT_USERNAME = "AAAAAAAAAA";
    private static final String UPDATED_USERNAME = "BBBBBBBBBB";

    private static final String DEFAULT_PASSWORD = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ADMINFLAG = false;
    private static final Boolean UPDATED_ADMINFLAG = true;

    private static final Boolean DEFAULT_DELETEFLAG = false;
    private static final Boolean UPDATED_DELETEFLAG = true;

    @Autowired
    private AppUserRepository appUserRepository;

    @Mock
    private AppUserRepository appUserRepositoryMock;

    /**
     * This repository is mocked in the com.mycompany.myapp.repository.search test package.
     *
     * @see com.mycompany.myapp.repository.search.AppUserSearchRepositoryMockConfiguration
     */
    @Autowired
    private AppUserSearchRepository mockAppUserSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restAppUserMockMvc;

    private AppUser appUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final AppUserResource appUserResource = new AppUserResource(appUserRepository, mockAppUserSearchRepository);
        this.restAppUserMockMvc = MockMvcBuilders.standaloneSetup(appUserResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AppUser createEntity(EntityManager em) {
        AppUser appUser = new AppUser()
            .username(DEFAULT_USERNAME)
            .password(DEFAULT_PASSWORD)
            .adminflag(DEFAULT_ADMINFLAG)
            .deleteflag(DEFAULT_DELETEFLAG);
        return appUser;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AppUser createUpdatedEntity(EntityManager em) {
        AppUser appUser = new AppUser()
            .username(UPDATED_USERNAME)
            .password(UPDATED_PASSWORD)
            .adminflag(UPDATED_ADMINFLAG)
            .deleteflag(UPDATED_DELETEFLAG);
        return appUser;
    }

    @BeforeEach
    public void initTest() {
        appUser = createEntity(em);
    }

    @Test
    @Transactional
    public void createAppUser() throws Exception {
        int databaseSizeBeforeCreate = appUserRepository.findAll().size();

        // Create the AppUser
        restAppUserMockMvc.perform(post("/api/app-users")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(appUser)))
            .andExpect(status().isCreated());

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeCreate + 1);
        AppUser testAppUser = appUserList.get(appUserList.size() - 1);
        assertThat(testAppUser.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testAppUser.getPassword()).isEqualTo(DEFAULT_PASSWORD);
        assertThat(testAppUser.isAdminflag()).isEqualTo(DEFAULT_ADMINFLAG);
        assertThat(testAppUser.isDeleteflag()).isEqualTo(DEFAULT_DELETEFLAG);

        // Validate the AppUser in Elasticsearch
        verify(mockAppUserSearchRepository, times(1)).save(testAppUser);
    }

    @Test
    @Transactional
    public void createAppUserWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = appUserRepository.findAll().size();

        // Create the AppUser with an existing ID
        appUser.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restAppUserMockMvc.perform(post("/api/app-users")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(appUser)))
            .andExpect(status().isBadRequest());

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeCreate);

        // Validate the AppUser in Elasticsearch
        verify(mockAppUserSearchRepository, times(0)).save(appUser);
    }


    @Test
    @Transactional
    public void getAllAppUsers() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList
        restAppUserMockMvc.perform(get("/api/app-users?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME)))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD)))
            .andExpect(jsonPath("$.[*].adminflag").value(hasItem(DEFAULT_ADMINFLAG.booleanValue())))
            .andExpect(jsonPath("$.[*].deleteflag").value(hasItem(DEFAULT_DELETEFLAG.booleanValue())));
    }
    
    @SuppressWarnings({"unchecked"})
    public void getAllAppUsersWithEagerRelationshipsIsEnabled() throws Exception {
        AppUserResource appUserResource = new AppUserResource(appUserRepositoryMock, mockAppUserSearchRepository);
        when(appUserRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        MockMvc restAppUserMockMvc = MockMvcBuilders.standaloneSetup(appUserResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restAppUserMockMvc.perform(get("/api/app-users?eagerload=true"))
        .andExpect(status().isOk());

        verify(appUserRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllAppUsersWithEagerRelationshipsIsNotEnabled() throws Exception {
        AppUserResource appUserResource = new AppUserResource(appUserRepositoryMock, mockAppUserSearchRepository);
            when(appUserRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));
            MockMvc restAppUserMockMvc = MockMvcBuilders.standaloneSetup(appUserResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restAppUserMockMvc.perform(get("/api/app-users?eagerload=true"))
        .andExpect(status().isOk());

            verify(appUserRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    public void getAppUser() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get the appUser
        restAppUserMockMvc.perform(get("/api/app-users/{id}", appUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(appUser.getId().intValue()))
            .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME))
            .andExpect(jsonPath("$.password").value(DEFAULT_PASSWORD))
            .andExpect(jsonPath("$.adminflag").value(DEFAULT_ADMINFLAG.booleanValue()))
            .andExpect(jsonPath("$.deleteflag").value(DEFAULT_DELETEFLAG.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingAppUser() throws Exception {
        // Get the appUser
        restAppUserMockMvc.perform(get("/api/app-users/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAppUser() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        int databaseSizeBeforeUpdate = appUserRepository.findAll().size();

        // Update the appUser
        AppUser updatedAppUser = appUserRepository.findById(appUser.getId()).get();
        // Disconnect from session so that the updates on updatedAppUser are not directly saved in db
        em.detach(updatedAppUser);
        updatedAppUser
            .username(UPDATED_USERNAME)
            .password(UPDATED_PASSWORD)
            .adminflag(UPDATED_ADMINFLAG)
            .deleteflag(UPDATED_DELETEFLAG);

        restAppUserMockMvc.perform(put("/api/app-users")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedAppUser)))
            .andExpect(status().isOk());

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
        AppUser testAppUser = appUserList.get(appUserList.size() - 1);
        assertThat(testAppUser.getUsername()).isEqualTo(UPDATED_USERNAME);
        assertThat(testAppUser.getPassword()).isEqualTo(UPDATED_PASSWORD);
        assertThat(testAppUser.isAdminflag()).isEqualTo(UPDATED_ADMINFLAG);
        assertThat(testAppUser.isDeleteflag()).isEqualTo(UPDATED_DELETEFLAG);

        // Validate the AppUser in Elasticsearch
        verify(mockAppUserSearchRepository, times(1)).save(testAppUser);
    }

    @Test
    @Transactional
    public void updateNonExistingAppUser() throws Exception {
        int databaseSizeBeforeUpdate = appUserRepository.findAll().size();

        // Create the AppUser

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAppUserMockMvc.perform(put("/api/app-users")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(appUser)))
            .andExpect(status().isBadRequest());

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AppUser in Elasticsearch
        verify(mockAppUserSearchRepository, times(0)).save(appUser);
    }

    @Test
    @Transactional
    public void deleteAppUser() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        int databaseSizeBeforeDelete = appUserRepository.findAll().size();

        // Delete the appUser
        restAppUserMockMvc.perform(delete("/api/app-users/{id}", appUser.getId())
            .accept(TestUtil.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the AppUser in Elasticsearch
        verify(mockAppUserSearchRepository, times(1)).deleteById(appUser.getId());
    }

    @Test
    @Transactional
    public void searchAppUser() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);
        when(mockAppUserSearchRepository.search(queryStringQuery("id:" + appUser.getId())))
            .thenReturn(Collections.singletonList(appUser));
        // Search the appUser
        restAppUserMockMvc.perform(get("/api/_search/app-users?query=id:" + appUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME)))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD)))
            .andExpect(jsonPath("$.[*].adminflag").value(hasItem(DEFAULT_ADMINFLAG.booleanValue())))
            .andExpect(jsonPath("$.[*].deleteflag").value(hasItem(DEFAULT_DELETEFLAG.booleanValue())));
    }
}
