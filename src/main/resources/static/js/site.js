(() => {
    const COLLECTION_LAYOUTS = {
        "student-list": {
            minCardWidth: 240,
            gap: 24,
            rows: 3,
            singleColumnRows: 6
        },
        "project-list": {
            minCardWidth: 480,
            gap: 25,
            rows: 3,
            singleColumnRows: 4
        },
        "publication-list": {
            minCardWidth: 2000,
            gap: 20,
            rows: 6,
            singleColumnRows: 6
        },
        "news-list": {
            minCardWidth: 2000,
            gap: 28,
            rows: 4,
            singleColumnRows: 4
        }
    };

    let studentsRequest = null;
    let studentDirectory = new Map();

    const collectionState = {
        students: {items: [], page: 1},
        projects: {items: [], page: 1},
        publications: {items: [], page: 1},
        news: {items: [], page: 1}
    };

    document.addEventListener(
        "DOMContentLoaded",
        () => {
            initializeCollectionControls();

            if (document.getElementById("student-list")) {
                loadStudents();
            }

            if (document.getElementById("project-list")) {
                loadProjects();
            }

            if (document.getElementById("publication-list")) {
                loadPublications();
            }

            if (document.getElementById("news-list")) {
                loadNews();
            }

            initializeContactForm();
            initializeResponsivePagination();
        }
    );

    async function fetchJson(url) {
        const response = await fetch(
            url,
            {
                method: "GET",
                headers: {
                    "Accept": "application/json"
                }
            }
        );

        if (!response.ok) {
            throw new Error(
                "HTTP durum kodu: " + response.status
            );
        }

        return response.json();
    }

    /*
     * STUDENTS
     */

    async function getStudents() {
        if (!studentsRequest) {
            studentsRequest = fetchJson("/api/students")
                .then(students => {
                    const studentList =
                        Array.isArray(students)
                            ? students
                            : [];

                    studentDirectory = new Map(
                        studentList
                            .filter(student => student.id)
                            .map(student => [student.id, student])
                    );

                    return studentList;
                })
                .catch(error => {
                    studentsRequest = null;
                    throw error;
                });
        }

        return studentsRequest;
    }

    async function loadStudents() {
        const container = document.getElementById("student-list");

        try {
            const students = await getStudents();
            collectionState.students.items = Array.isArray(students)
                ? students.filter(student => student.active !== false)
                : [];
            populateDepartmentFilter(collectionState.students.items);
            renderStudents();
        } catch (error) {
            console.error(error);
            showMessage(container, "Ekip üyeleri yüklenirken bir hata oluştu.", true);
        }
    }

    function renderStudents() {
        const state = collectionState.students;
        const searchInput = document.getElementById("student-search");
        const departmentFilter = document.getElementById("student-department-filter");
        const memberTypeFilter = document.getElementById("student-member-type-filter");
        const query = normalizeSearchValue(searchInput ? searchInput.value : "");
        const department = departmentFilter ? departmentFilter.value : "ALL";
        const memberType = memberTypeFilter ? memberTypeFilter.value : "ALL";

        const filtered = state.items
            .filter(member =>
                memberType === "ALL"
                || resolveMemberType(member) === memberType
            )
            .filter(member =>
                department === "ALL"
                || member.department === department
            )
            .filter(member => includesSearch([
                member.firstName,
                member.lastName,
                member.studentNumber,
                member.department,
                member.currentTask,
                member.email,
                member.academicTitle,
                formatMemberType(resolveMemberType(member))
            ], query))
            .sort((first, second) => {
                const typeDifference =
                    memberTypeSortOrder(first)
                    - memberTypeSortOrder(second);

                if (typeDifference !== 0) {
                    return typeDifference;
                }

                return compareDateValuesDescending(
                    first.createdAt,
                    second.createdAt
                );
            });

        renderCollectionPage({
            state,
            filtered,
            listId: "student-list",
            paginationId: "student-pagination",
            countId: "student-result-count",
            emptyMessage: "Filtreye uygun ekip üyesi bulunmuyor.",
            createCard: createStudentCard,
            rerender: renderStudents
        });
    }

    function createStudentCard(member) {
        const card = document.createElement("article");
        card.className = "student-card";

        const timestamp = createCardTimestamp(member.createdAt);
        if (timestamp) {
            card.appendChild(timestamp);
        }

        const profileUrl = createMemberProfileUrl(member);

        card.setAttribute("role", "link");
        card.tabIndex = 0;
        card.setAttribute(
            "aria-label",
            createMemberDisplayName(member) + " profilini aç"
        );

        const openProfile = () => {
            window.location.href = profileUrl;
        };

        card.addEventListener("click", event => {
            if (event.target.closest("a, button, input, select, textarea")) {
                return;
            }
            openProfile();
        });

        card.addEventListener("keydown", event => {
            if (event.target !== card) {
                return;
            }

            if (event.key === "Enter" || event.key === " ") {
                event.preventDefault();
                openProfile();
            }
        });

        const photoContainer = document.createElement("div");
        photoContainer.className = "student-photo-container";

        if (member.photoPath) {
            const image = document.createElement("img");
            image.className = "student-photo";
            image.src = member.photoPath;
            image.loading = "lazy";
            image.alt = createMemberDisplayName(member) + " fotoğrafı";

            image.addEventListener("error", () => {
                photoContainer.replaceChildren(
                    createStudentPlaceholder(member)
                );
            });

            photoContainer.appendChild(image);
        } else {
            photoContainer.appendChild(
                createStudentPlaceholder(member)
            );
        }

        const photoLink = document.createElement("a");
        photoLink.className = "student-photo-link";
        photoLink.href = profileUrl;
        photoLink.setAttribute(
            "aria-label",
            createMemberDisplayName(member) + " profilini aç"
        );
        photoLink.appendChild(photoContainer);

        const info = document.createElement("div");
        info.className = "student-info";

        const topRow = document.createElement("div");
        topRow.className = "member-card-top-row";

        const nameLink = document.createElement("a");
        nameLink.className = "student-name-link";
        nameLink.href = profileUrl;

        const name = document.createElement("h3");
        name.className = "student-name";
        name.textContent = createMemberDisplayName(member);
        nameLink.appendChild(name);

        const typeBadge = document.createElement("span");
        const resolvedType = resolveMemberType(member);
        typeBadge.className =
            "member-type-badge "
            + resolvedType.toLocaleLowerCase(window.I18n.getLocale());
        typeBadge.textContent = formatMemberType(resolvedType);

        topRow.append(nameLink, typeBadge);

        const department = document.createElement("p");
        department.className = "student-department";
        department.textContent =
            member.department
            || "Bölüm bilgisi bulunmuyor.";

        const task = document.createElement("p");
        task.className = "student-task";
        task.textContent =
            member.currentTask
            || "Güncel çalışma bilgisi bulunmuyor.";

        info.append(topRow, department, task);

        if (member.email) {
            const email = document.createElement("a");
            email.className = "student-email";
            email.href = "mailto:" + member.email;
            email.textContent = member.email;
            info.appendChild(email);
        }

        card.append(photoLink, info);
        return card;
    }

    function createStudentFullName(member) {
        return createMemberDisplayName(member);
    }

    function createStudentProfileUrl(memberId) {
        const member = studentDirectory.get(memberId);
        return createMemberProfileUrl(
            member || {id: memberId, memberType: "STUDENT"}
        );
    }

    function resolveMemberType(member) {
        return member && member.memberType === "ACADEMICIAN"
            ? "ACADEMICIAN"
            : "STUDENT";
    }

    function formatMemberType(memberType) {
        return memberType === "ACADEMICIAN"
            ? "Akademisyen"
            : "Öğrenci";
    }

    function memberTypeSortOrder(member) {
        return resolveMemberType(member) === "ACADEMICIAN"
            ? 0
            : 1;
    }

    function createMemberDisplayName(member) {
        const nameParts = [
            member && member.firstName,
            member && member.lastName
        ].filter(Boolean);

        const fullName = nameParts.length > 0
            ? nameParts.join(" ")
            : "İsimsiz ekip üyesi";

        if (
            resolveMemberType(member) === "ACADEMICIAN"
            && member
            && member.academicTitle
        ) {
            return member.academicTitle.trim() + " " + fullName;
        }

        return fullName;
    }

    function createMemberProfileUrl(member) {
        const page =
            resolveMemberType(member) === "ACADEMICIAN"
                ? "/academic.html"
                : "/student.html";

        return page
            + "?id="
            + encodeURIComponent(member && member.id ? member.id : "");
    }

    function createStudentPlaceholder(student) {
        const placeholder =
            document.createElement("div");

        placeholder.className =
            "student-photo-placeholder";

        const firstInitial =
            student.firstName
                ? student.firstName
                    .charAt(0)
                    .toLocaleUpperCase(window.I18n.getLocale())
                : "";

        const lastInitial =
            student.lastName
                ? student.lastName
                    .charAt(0)
                    .toLocaleUpperCase(window.I18n.getLocale())
                : "";

        placeholder.textContent =
            firstInitial + lastInitial || "?";

        return placeholder;
    }

    /*
     * PROJECTS
     */

    async function loadProjects() {
        const container = document.getElementById("project-list");

        try {
            const projects = await fetchJson("/api/projects");
            try {
                await getStudents();
            } catch (studentError) {
                console.error("Katkıda bulunan öğrenciler yüklenemedi.", studentError);
            }

            collectionState.projects.items = Array.isArray(projects) ? projects : [];
            renderProjects();
        } catch (error) {
            console.error(error);
            showMessage(container, "Projeler yüklenirken bir hata oluştu.", true);
        }
    }

    function renderProjects() {
        const state = collectionState.projects;
        const searchInput = document.getElementById("project-search");
        const statusFilter = document.getElementById("project-status-filter");
        const query = normalizeSearchValue(searchInput ? searchInput.value : "");
        const status = statusFilter ? statusFilter.value : "ALL";

        const filtered = state.items
            .filter(project => status === "ALL" || project.status === status)
            .filter(project => {
                const contributorNames = Array.from(project.studentIds || [])
                    .map(id => studentDirectory.get(id))
                    .filter(Boolean)
                    .map(createStudentFullName);
                return includesSearch([
                    project.name, project.summary, project.description, project.status,
                    ...(project.technologies || []), ...contributorNames
                ], query);
            })
            .sort((first, second) => compareDateValuesDescending(
                first.createdAt || first.startDate,
                second.createdAt || second.startDate
            ));

        renderCollectionPage({
            state, filtered, listId: "project-list", paginationId: "project-pagination",
            countId: "project-result-count", emptyMessage: "Filtreye uygun proje bulunmuyor.",
            createCard: createProjectCard, rerender: renderProjects
        });
    }

    function createProjectCard(project) {
        const card =
            document.createElement("article");

        card.className = "project-card";

        const timestamp = createCardTimestamp(project.createdAt || project.startDate);
        if (timestamp) {
            card.appendChild(timestamp);
        }

        const imageContainer =
            document.createElement("div");

        imageContainer.className =
            "project-image-container";

        if (project.imagePath) {
            const image =
                document.createElement("img");

            image.className = "project-image";
            image.src = project.imagePath;
            image.loading = "lazy";

            image.alt =
                (project.name || "Proje")
                + " görseli";

            image.addEventListener(
                "error",
                () => {
                    imageContainer.replaceChildren(
                        createProjectPlaceholder(project)
                    );
                }
            );

            imageContainer.appendChild(image);

        } else {
            imageContainer.appendChild(
                createProjectPlaceholder(project)
            );
        }

        const content =
            document.createElement("div");

        content.className = "project-content";

        const topRow =
            document.createElement("div");

        topRow.className = "project-top-row";

        const statusBadge =
            document.createElement("span");

        statusBadge.className =
            "status-badge "
            + createStatusClass(project.status);

        statusBadge.textContent =
            formatProjectStatus(project.status);

        topRow.appendChild(statusBadge);

        if (project.featured) {
            const featured =
                document.createElement("span");

            featured.className = "featured-badge";
            featured.textContent = "Öne Çıkan";

            topRow.appendChild(featured);
        }

        const title =
            document.createElement("h3");

        title.className = "project-title";
        title.textContent =
            project.name || "İsimsiz proje";

        const summary =
            document.createElement("p");

        summary.className = "project-summary";
        summary.textContent =
            project.summary
            || "Proje özeti bulunmuyor.";

        const dates =
            document.createElement("p");

        dates.className = "project-dates";
        dates.textContent =
            createProjectDateText(project);

        content.append(
            topRow,
            title,
            summary,
            dates
        );

        const technologies =
            Array.isArray(project.technologies)
                ? project.technologies.filter(Boolean)
                : [];

        if (technologies.length > 0) {
            const technologyList =
                document.createElement("ul");

            technologyList.className =
                "technology-list";

            technologies.forEach(technology => {
                const item =
                    document.createElement("li");

                item.className = "technology-tag";
                item.textContent = technology;

                technologyList.appendChild(item);
            });

            content.appendChild(technologyList);
        }

        if (project.description) {
            const description =
                document.createElement("p");

            description.className = "details-text";
            description.textContent = project.description;
            description.hidden = true;

            content.appendChild(description);

            content.appendChild(
                createToggleButton(
                    description,
                    "Proje Detayları",
                    "Detayları Gizle"
                )
            );
        }

        const safeProjectUrl =
            getSafeHttpUrl(project.projectUrl);

        if (safeProjectUrl) {
            const linkContainer =
                document.createElement("div");

            linkContainer.className =
                "project-link-container";

            linkContainer.appendChild(
                createExternalLink(
                    safeProjectUrl,
                    "Proje Bağlantısını Aç →"
                )
            );

            content.appendChild(linkContainer);
        }

        const body =
            document.createElement("div");

        body.className = "project-body";
        body.append(
            content,
            createProjectContributors(project)
        );

        card.append(
            imageContainer,
            body
        );

        return card;
    }

    function createProjectContributors(project) {
        const aside = document.createElement("aside");
        aside.className = "project-contributors";
        aside.setAttribute(
            "aria-label",
            "Projeye katkıda bulunan ekip üyeleri"
        );

        const title = document.createElement("h4");
        title.className = "project-contributors-title";
        title.textContent = "Katkıda Bulunanlar";
        aside.appendChild(title);

        const memberIds = Array.isArray(project.studentIds)
            ? project.studentIds
            : [];

        const contributors = memberIds
            .map(memberId => studentDirectory.get(memberId))
            .filter(Boolean);

        const academics = contributors
            .filter(member => resolveMemberType(member) === "ACADEMICIAN");

        const students = contributors
            .filter(member => resolveMemberType(member) === "STUDENT");

        if (contributors.length === 0) {
            const empty = document.createElement("p");
            empty.className = "contributors-empty";
            empty.textContent = "Henüz ekip üyesi bağlantısı eklenmemiş.";
            aside.appendChild(empty);
            return aside;
        }

        if (academics.length > 0) {
            aside.appendChild(
                createContributorGroup(
                    "Akademisyenler",
                    academics,
                    "academician"
                )
            );
        }

        if (students.length > 0) {
            aside.appendChild(
                createContributorGroup(
                    "Öğrenciler",
                    students,
                    "student"
                )
            );
        }

        return aside;
    }

    function createContributorGroup(
        labelText,
        members,
        typeClass
    ) {
        const group = document.createElement("section");
        group.className =
            "contributor-group contributor-group-" + typeClass;

        const label = document.createElement("h5");
        label.className = "contributor-group-title";
        label.textContent = labelText;

        const list = document.createElement("ul");
        list.className = "contributor-list";

        members.forEach(member => {
            const item = document.createElement("li");
            item.appendChild(createContributorLink(member));
            list.appendChild(item);
        });

        group.append(label, list);
        return group;
    }

    function createContributorLink(member) {
        const link = document.createElement("a");
        const memberType = resolveMemberType(member);

        link.className =
            "contributor-link "
            + (
                memberType === "ACADEMICIAN"
                    ? "academician"
                    : "student"
            );
        link.href = createMemberProfileUrl(member);
        link.title = createMemberDisplayName(member) + " profilini aç";

        const avatar = document.createElement("span");
        avatar.className = "contributor-avatar";

        if (member.photoPath) {
            const image = document.createElement("img");
            image.src = member.photoPath;
            image.loading = "lazy";
            image.alt = "";

            image.addEventListener("error", () => {
                avatar.replaceChildren(
                    document.createTextNode(
                        createStudentInitials(member)
                    )
                );
            });

            avatar.appendChild(image);
        } else {
            avatar.textContent = createStudentInitials(member);
        }

        const name = document.createElement("span");
        name.className = "contributor-name";
        name.textContent =
            member.firstName
            || member.lastName
            || formatMemberType(memberType);

        link.append(avatar, name);
        return link;
    }

    function createStudentInitials(student) {
        const firstInitial = student.firstName
            ? student.firstName.charAt(0)
                .toLocaleUpperCase(window.I18n.getLocale())
            : "";

        const lastInitial = student.lastName
            ? student.lastName.charAt(0)
                .toLocaleUpperCase(window.I18n.getLocale())
            : "";

        return firstInitial + lastInitial || "?";
    }

    function createProjectPlaceholder(project) {
        const placeholder =
            document.createElement("div");

        placeholder.className =
            "project-image-placeholder";

        placeholder.textContent =
            project.name || "Laboratuvar Projesi";

        return placeholder;
    }

    function createProjectDateText(project) {
        const startDate =
            formatDate(project.startDate);

        const endDate =
            formatDate(project.endDate);

        if (startDate && endDate) {
            return startDate + " – " + endDate;
        }

        if (startDate) {
            return "Başlangıç: " + startDate;
        }

        if (endDate) {
            return "Bitiş: " + endDate;
        }

        return "Tarih bilgisi bulunmuyor.";
    }

    function formatProjectStatus(status) {
        const statusNames = {
            PLANNED: "Planlandı",
            IN_PROGRESS: "Devam Ediyor",
            COMPLETED: "Tamamlandı",
            ON_HOLD: "Beklemede",
            CANCELLED: "İptal Edildi"
        };

        return statusNames[status]
            || formatEnumValue(status)
            || "Durum Belirtilmedi";
    }

    function createStatusClass(status) {
        const classes = {
            COMPLETED: "completed",
            IN_PROGRESS: "in-progress",
            PLANNED: "planned",
            CANCELLED: "cancelled"
        };

        return classes[status] || "";
    }

    /*
     * PUBLICATIONS
     */

    async function loadPublications() {
        const container = document.getElementById("publication-list");

        try {
            const publications = await fetchJson("/api/publications");
            collectionState.publications.items = Array.isArray(publications) ? publications : [];
            renderPublications();
        } catch (error) {
            console.error(error);
            showMessage(container, "Yayınlar yüklenirken bir hata oluştu.", true);
        }
    }

    function renderPublications() {
        const state = collectionState.publications;
        const searchInput = document.getElementById("publication-search");
        const typeFilter = document.getElementById("publication-type-filter");
        const query = normalizeSearchValue(searchInput ? searchInput.value : "");
        const type = typeFilter ? typeFilter.value : "ALL";

        const filtered = state.items
            .filter(publication => type === "ALL" || publication.type === type)
            .filter(publication => includesSearch([
                publication.title, publication.venue, publication.publicationYear,
                publication.type, publication.abstractText, ...(publication.authors || [])
            ], query))
            .sort((first, second) => compareDateValuesDescending(
                first.createdAt || String(first.publicationYear || ""),
                second.createdAt || String(second.publicationYear || "")
            ));

        renderCollectionPage({
            state, filtered, listId: "publication-list", paginationId: "publication-pagination",
            countId: "publication-result-count", emptyMessage: "Filtreye uygun yayın bulunmuyor.",
            createCard: createPublicationCard, rerender: renderPublications
        });
    }

    function createPublicationCard(publication) {
        const card =
            document.createElement("article");

        card.className = "publication-card";

        const timestamp = createCardTimestamp(publication.createdAt || (publication.publicationYear ? String(publication.publicationYear) : null));
        if (timestamp) {
            card.appendChild(timestamp);
        }

        const headerRow =
            document.createElement("div");

        headerRow.className =
            "publication-header-row";

        const type =
            document.createElement("span");

        type.className = "publication-type";

        type.textContent =
            formatPublicationType(publication.type);

        headerRow.appendChild(type);

        if (publication.featured) {
            const featured =
                document.createElement("span");

            featured.className = "featured-badge";
            featured.textContent = "Öne Çıkan";

            headerRow.appendChild(featured);
        }

        const title =
            document.createElement("h3");

        title.className = "publication-title";

        title.textContent =
            publication.title || "Başlıksız yayın";

        const authors =
            document.createElement("p");

        authors.className =
            "publication-authors";

        authors.textContent =
            Array.isArray(publication.authors)
            && publication.authors.length > 0
                ? publication.authors.join(", ")
                : "Yazar bilgisi bulunmuyor.";

        const venue =
            document.createElement("p");

        venue.className = "publication-venue";

        venue.textContent =
            createPublicationVenueText(publication);

        card.append(
            headerRow,
            title,
            authors,
            venue
        );

        if (publication.abstractText) {
            const abstract =
                document.createElement("p");

            abstract.className = "details-text";
            abstract.textContent =
                publication.abstractText;

            abstract.hidden = true;

            card.appendChild(abstract);

            card.appendChild(
                createToggleButton(
                    abstract,
                    "Özeti Göster",
                    "Özeti Gizle"
                )
            );
        }

        const links =
            document.createElement("div");

        links.className = "publication-links";

        const doiUrl =
            createDoiUrl(publication.doi);

        if (doiUrl) {
            links.appendChild(
                createExternalLink(
                    doiUrl,
                    "DOI Sayfası"
                )
            );
        }

        const publicationUrl =
            getSafeHttpUrl(
                publication.publicationUrl
            );

        if (publicationUrl) {
            links.appendChild(
                createExternalLink(
                    publicationUrl,
                    "Yayını Aç →"
                )
            );
        }

        if (links.childElementCount > 0) {
            card.appendChild(links);
        }

        return card;
    }

    function createPublicationVenueText(publication) {
        const values = [];

        if (publication.venue) {
            values.push(publication.venue);
        }

        if (publication.publicationYear) {
            values.push(
                String(publication.publicationYear)
            );
        }

        return values.length > 0
            ? values.join(" • ")
            : "Yayın yeri bilgisi bulunmuyor.";
    }

    function formatPublicationType(type) {
        const typeNames = {
            JOURNAL_ARTICLE: "Dergi Makalesi",
            CONFERENCE_PAPER: "Konferans Bildirisi",
            BOOK_CHAPTER: "Kitap Bölümü",
            BOOK: "Kitap",
            THESIS: "Tez",
            TECHNICAL_REPORT: "Teknik Rapor",
            REPORT: "Rapor",
            OTHER: "Diğer"
        };

        return typeNames[type]
            || formatEnumValue(type)
            || "Akademik Yayın";
    }

    function createDoiUrl(doi) {
        if (!doi || typeof doi !== "string") {
            return null;
        }

        const normalizedDoi = doi
            .trim()
            .replace(
                /^https?:\/\/(dx\.)?doi\.org\//i,
                ""
            );

        if (!normalizedDoi) {
            return null;
        }

        return getSafeHttpUrl(
            "https://doi.org/" + normalizedDoi
        );
    }

    /*
     * NEWS
     */

    async function loadNews() {
        const container = document.getElementById("news-list");

        try {
            const newsPosts = await fetchJson("/api/news/active");
            collectionState.news.items = Array.isArray(newsPosts) ? newsPosts : [];
            renderNews();
        } catch (error) {
            console.error(error);
            showMessage(container, "Haberler yüklenirken bir hata oluştu.", true);
        }
    }

    function renderNews() {
        const state = collectionState.news;
        const searchInput = document.getElementById("news-search");
        const categoryFilter = document.getElementById("news-category-filter");
        const query = normalizeSearchValue(searchInput ? searchInput.value : "");
        const category = categoryFilter ? categoryFilter.value : "ALL";

        const filtered = state.items
            .filter(newsPost => category === "ALL" || newsPost.category === category)
            .filter(newsPost => includesSearch([
                newsPost.title, newsPost.summary, newsPost.content, newsPost.category
            ], query))
            .sort((first, second) => compareDateValuesDescending(first.publishedAt, second.publishedAt));

        renderCollectionPage({
            state, filtered, listId: "news-list", paginationId: "news-pagination",
            countId: "news-result-count", emptyMessage: "Filtreye uygun haber bulunmuyor.",
            createCard: createNewsCard, rerender: renderNews
        });
    }

    function createNewsCard(newsPost) {
        const card =
            document.createElement("article");

        card.className = "news-card";

        const timestamp = createCardTimestamp(newsPost.publishedAt);
        if (timestamp) {
            card.appendChild(timestamp);
        }

        const imageContainer =
            document.createElement("div");

        imageContainer.className =
            "news-image-container";

        if (newsPost.imagePath) {
            const image =
                document.createElement("img");

            image.className = "news-image";
            image.src = newsPost.imagePath;
            image.loading = "lazy";

            image.alt =
                (newsPost.title || "Haber")
                + " görseli";

            image.addEventListener(
                "error",
                () => {
                    imageContainer.replaceChildren(
                        createNewsPlaceholder(newsPost)
                    );
                }
            );

            imageContainer.appendChild(image);

        } else {
            imageContainer.appendChild(
                createNewsPlaceholder(newsPost)
            );
        }

        const content =
            document.createElement("div");

        content.className = "news-content";

        const meta =
            document.createElement("p");

        meta.className = "news-meta";
        meta.textContent =
            createNewsMeta(newsPost);

        const title =
            document.createElement("h3");

        title.className = "news-title";
        title.textContent =
            newsPost.title || "Başlıksız haber";

        const summary =
            document.createElement("p");

        summary.className = "news-summary";
        summary.textContent =
            newsPost.summary || "";

        content.append(
            meta,
            title,
            summary
        );

        if (newsPost.content) {
            const fullContent =
                document.createElement("p");

            fullContent.className = "details-text";
            fullContent.textContent = newsPost.content;
            fullContent.hidden = true;

            content.appendChild(fullContent);

            content.appendChild(
                createToggleButton(
                    fullContent,
                    "Devamını Oku →",
                    "İçeriği Gizle"
                )
            );
        }

        card.append(
            imageContainer,
            content
        );

        return card;
    }

    function createNewsPlaceholder(newsPost) {
        const placeholder =
            document.createElement("div");

        placeholder.className =
            "news-image-placeholder";

        placeholder.textContent =
            formatNewsCategory(
                newsPost.category
            );

        return placeholder;
    }

    function createNewsMeta(newsPost) {
        const values = [];

        if (newsPost.category) {
            values.push(
                formatNewsCategory(
                    newsPost.category
                )
            );
        }

        const date =
            formatDate(newsPost.publishedAt);

        if (date) {
            values.push(date);
        }

        return values.join(" • ");
    }

    function formatNewsCategory(category) {
        const categoryNames = {
            NEWS: "Haber",
            ANNOUNCEMENT: "Duyuru",
            PROJECT_UPDATE: "Proje Güncellemesi",
            PUBLICATION: "Yayın",
            EVENT: "Etkinlik",
            STUDENT_ACTIVITY: "Öğrenci Etkinliği"
        };

        return categoryNames[category]
            || formatEnumValue(category)
            || "Haber";
    }

    function initializeCollectionControls() {
        const bindings = [
            ["student-search", "input", "students", renderStudents],
            ["student-member-type-filter", "change", "students", renderStudents],
            ["student-department-filter", "change", "students", renderStudents],
            ["project-search", "input", "projects", renderProjects],
            ["project-status-filter", "change", "projects", renderProjects],
            ["publication-search", "input", "publications", renderPublications],
            ["publication-type-filter", "change", "publications", renderPublications],
            ["news-search", "input", "news", renderNews],
            ["news-category-filter", "change", "news", renderNews]
        ];

        bindings.forEach(([id, eventName, stateKey, renderFunction]) => {
            const element = document.getElementById(id);

            if (!element) {
                return;
            }

            element.addEventListener(eventName, () => {
                collectionState[stateKey].page = 1;
                renderFunction();
            });
        });
    }

    function populateDepartmentFilter(students) {
        const select = document.getElementById("student-department-filter");

        if (!select) {
            return;
        }

        const currentValue = select.value;
        const departments = [...new Set(
            students.map(student => student.department).filter(Boolean)
        )].sort((a, b) => a.localeCompare(b, "tr"));

        select.replaceChildren(new Option("Tüm bölümler", "ALL"));
        departments.forEach(department => select.add(new Option(department, department)));
        select.value = departments.includes(currentValue) ? currentValue : "ALL";
    }

    function renderCollectionPage({
        state,
        filtered,
        listId,
        paginationId,
        countId,
        emptyMessage,
        createCard,
        rerender
    }) {
        const container = document.getElementById(listId);

        if (!container) {
            return;
        }

        const previewLimit = Number(
            container.dataset.previewLimit || 0
        );

        const pageSize = previewLimit > 0
            ? previewLimit
            : calculateAdaptivePageSize(
                container,
                listId
            );

        if (
            state.pageSize
            && state.pageSize !== pageSize
        ) {
            const previousStartIndex =
                (Math.max(state.page, 1) - 1)
                * state.pageSize;

            state.page =
                Math.floor(
                    previousStartIndex / pageSize
                ) + 1;
        }

        state.pageSize = pageSize;

        const totalPages = Math.max(
            1,
            Math.ceil(filtered.length / pageSize)
        );

        state.page = Math.min(
            Math.max(state.page, 1),
            totalPages
        );

        const startIndex =
            (state.page - 1) * pageSize;

        const visibleItems = previewLimit > 0
            ? filtered.slice(0, previewLimit)
            : filtered.slice(
                startIndex,
                startIndex + pageSize
            );

        container.replaceChildren();

        const countElement =
            document.getElementById(countId);

        if (countElement) {
            countElement.textContent =
                filtered.length
                + " kayıt • "
                + (
                    previewLimit > 0
                        ? "En yeni kayıtlar"
                        : pageSize
                            + " kart/sayfa • En yeni önce"
                );
        }

        if (filtered.length === 0) {
            showMessage(container, emptyMessage);
        } else {
            visibleItems.forEach(item => {
                container.appendChild(
                    createCard(item)
                );
            });
        }

        if (previewLimit <= 0) {
            renderPagination(
                paginationId,
                state,
                filtered.length,
                pageSize,
                rerender
            );
        }
    }

    function renderPagination(
        containerId,
        state,
        itemCount,
        pageSize,
        rerender
    ) {
        const container = document.getElementById(containerId);

        if (!container) {
            return;
        }

        container.replaceChildren();

        const totalPages = Math.ceil(itemCount / pageSize);

        if (totalPages <= 1) {
            return;
        }

        container.appendChild(
            createPageButton(
                "Önceki",
                state.page - 1,
                state.page === 1,
                false
            )
        );

        const firstPage = Math.max(1, state.page - 2);
        const lastPage = Math.min(totalPages, firstPage + 4);
        const adjustedFirst = Math.max(1, lastPage - 4);

        for (
            let page = adjustedFirst;
            page <= lastPage;
            page += 1
        ) {
            container.appendChild(
                createPageButton(
                    String(page),
                    page,
                    false,
                    page === state.page
                )
            );
        }

        container.appendChild(
            createPageButton(
                "Sonraki",
                state.page + 1,
                state.page === totalPages,
                false
            )
        );

        function createPageButton(
            label,
            page,
            disabled,
            active
        ) {
            const button = document.createElement("button");
            button.type = "button";
            button.textContent = label;
            button.disabled = disabled;
            button.classList.toggle("active", active);
            button.setAttribute(
                "aria-current",
                active ? "page" : "false"
            );

            button.addEventListener("click", () => {
                state.page = page;
                rerender();

                document
                    .getElementById(containerId)
                    .previousElementSibling
                    ?.scrollIntoView({
                        behavior: "smooth",
                        block: "start"
                    });
            });

            return button;
        }
    }

    function calculateAdaptivePageSize(
        container,
        listId
    ) {
        const layout =
            COLLECTION_LAYOUTS[listId]
            || {
                minCardWidth: 300,
                gap: 20,
                rows: 4,
                singleColumnRows: 5
            };

        const width =
            Math.max(
                container.clientWidth,
                container.getBoundingClientRect().width,
                1
            );

        const columns =
            Math.max(
                1,
                Math.floor(
                    (width + layout.gap)
                    / (layout.minCardWidth + layout.gap)
                )
            );

        const rows =
            columns === 1
                ? layout.singleColumnRows
                : layout.rows;

        return Math.max(1, columns * rows);
    }

    function initializeResponsivePagination() {
        let resizeTimer = null;

        window.addEventListener("resize", () => {
            window.clearTimeout(resizeTimer);

            resizeTimer = window.setTimeout(() => {
                if (document.getElementById("student-list")) {
                    renderStudents();
                }

                if (document.getElementById("project-list")) {
                    renderProjects();
                }

                if (document.getElementById("publication-list")) {
                    renderPublications();
                }

                if (document.getElementById("news-list")) {
                    renderNews();
                }
            }, 180);
        });
    }

    function normalizeSearchValue(value) {
        return String(value || "").trim().toLocaleLowerCase(window.I18n.getLocale());
    }

    function includesSearch(values, query) {
        if (!query) {
            return true;
        }
        return values.some(value => normalizeSearchValue(value).includes(query));
    }

    function compareDateValuesDescending(firstValue, secondValue) {
        return toTimestamp(secondValue) - toTimestamp(firstValue);
    }

    function toTimestamp(value) {
        if (!value) {
            return 0;
        }
        if (/^\d{4}$/.test(String(value))) {
            return new Date(Number(value), 0, 1).getTime();
        }
        const timestamp = new Date(value).getTime();
        return Number.isNaN(timestamp) ? 0 : timestamp;
    }

    function createCardTimestamp(value) {
        const formatted = formatDateTime(value);
        if (!formatted) {
            return null;
        }

        const time = document.createElement("time");
        time.className = "card-timestamp";
        time.textContent = formatted;
        if (value) {
            time.dateTime = String(value);
        }
        return time;
    }

    function formatDateTime(value) {
        if (!value) {
            return "";
        }
        if (/^\d{4}$/.test(String(value))) {
            return String(value);
        }
        const date = new Date(value);
        if (Number.isNaN(date.getTime())) {
            return "";
        }
        const hasTime = String(value).includes("T");
        return new Intl.DateTimeFormat(
            window.I18n.getLocale(),
            hasTime
                ? {dateStyle: "short", timeStyle: "short"}
                : {dateStyle: "short"}
        ).format(date);
    }

    /*
     * CONTACT
     */

    function initializeContactForm() {
        const form =
            document.getElementById("contact-form");

        if (!form) {
            return;
        }

        form.addEventListener(
            "submit",
            submitContactMessage
        );
    }

    async function submitContactMessage(event) {
        event.preventDefault();

        const form = event.currentTarget;

        const submitButton =
            document.getElementById(
                "contact-submit-button"
            );

        const feedbackElement =
            document.getElementById(
                "contact-feedback"
            );

        if (!form.checkValidity()) {
            form.reportValidity();
            return;
        }

        const formData =
            new FormData(form);

        const requestBody = {
            senderName:
                String(
                    formData.get("senderName") || ""
                ).trim(),

            email:
                String(
                    formData.get("email") || ""
                ).trim(),

            subject:
                String(
                    formData.get("subject") || ""
                ).trim(),

            message:
                String(
                    formData.get("message") || ""
                ).trim()
        };

        if (
            !requestBody.senderName
            || !requestBody.email
            || !requestBody.subject
            || !requestBody.message
        ) {
            showContactFeedback(
                feedbackElement,
                "Lütfen bütün alanları doldurun.",
                true
            );

            return;
        }

        submitButton.disabled = true;
        submitButton.textContent =
            "Mesaj Gönderiliyor...";

        feedbackElement.hidden = true;

        try {
            const response = await fetch(
                "/api/contact-messages",
                {
                    method: "POST",

                    headers: {
                        "Accept": "application/json",
                        "Content-Type": "application/json"
                    },

                    body: JSON.stringify(requestBody)
                }
            );

            const responseBody =
                await readJsonSafely(response);

            if (!response.ok) {
                throw new Error(
                    getContactErrorMessage(
                        responseBody,
                        response.status
                    )
                );
            }

            form.reset();

            showContactFeedback(
                feedbackElement,
                "Mesajınız başarıyla gönderildi. "
                + "En kısa sürede sizinle iletişime geçilecektir.",
                false
            );

        } catch (error) {
            console.error(
                "İletişim formu gönderme hatası:",
                error
            );

            showContactFeedback(
                feedbackElement,
                error.message
                || "Mesaj gönderilirken bir hata oluştu.",
                true
            );

        } finally {
            submitButton.disabled = false;
            submitButton.textContent =
                "Mesaj Gönder";
        }
    }

    async function readJsonSafely(response) {
        try {
            return await response.json();
        } catch (error) {
            return null;
        }
    }

    function getContactErrorMessage(
        responseBody,
        status
    ) {
        if (
            responseBody
            && responseBody.validationErrors
            && typeof responseBody.validationErrors
                === "object"
        ) {
            const validationMessages =
                Object.values(
                    responseBody.validationErrors
                ).filter(Boolean);

            if (validationMessages.length > 0) {
                return validationMessages.join(" ");
            }
        }

        if (
            responseBody
            && typeof responseBody.message === "string"
            && responseBody.message.trim()
        ) {
            return responseBody.message;
        }

        if (status === 400) {
            return "Form bilgileri geçersiz. "
                + "Alanları kontrol edin.";
        }

        if (status === 401 || status === 403) {
            return "Mesaj gönderme işlemine izin verilmedi.";
        }

        if (status >= 500) {
            return "Sunucuda bir hata oluştu. "
                + "Daha sonra tekrar deneyin.";
        }

        return "Mesaj gönderilemedi. "
            + "HTTP durum kodu: "
            + status;
    }

    function showContactFeedback(
        feedbackElement,
        message,
        isError
    ) {
        feedbackElement.className =
            isError
                ? "contact-feedback error"
                : "contact-feedback success";

        feedbackElement.textContent = message;
        feedbackElement.hidden = false;
    }

    /*
     * COMMON
     */

    function createToggleButton(
        targetElement,
        showText,
        hideText
    ) {
        const button =
            document.createElement("button");

        button.type = "button";
        button.className = "toggle-button";
        button.textContent = showText;

        button.setAttribute(
            "aria-expanded",
            "false"
        );

        button.addEventListener(
            "click",
            () => {
                const shouldShow =
                    targetElement.hidden;

                targetElement.hidden =
                    !shouldShow;

                button.textContent =
                    shouldShow
                        ? hideText
                        : showText;

                button.setAttribute(
                    "aria-expanded",
                    String(shouldShow)
                );
            }
        );

        return button;
    }

    function createExternalLink(url, text) {
        const link =
            document.createElement("a");

        link.className = "external-link";
        link.href = url;
        link.textContent = text;
        link.target = "_blank";
        link.rel = "noopener noreferrer";

        return link;
    }

    function getSafeHttpUrl(value) {
        if (!value || typeof value !== "string") {
            return null;
        }

        try {
            const url = new URL(value.trim());

            if (
                url.protocol !== "http:"
                && url.protocol !== "https:"
            ) {
                return null;
            }

            return url.href;

        } catch (error) {
            return null;
        }
    }

    function formatDate(dateValue) {
        if (!dateValue) {
            return "";
        }

        const date = new Date(dateValue);

        if (Number.isNaN(date.getTime())) {
            return "";
        }

        return new Intl.DateTimeFormat(
            window.I18n.getLocale(),
            {
                day: "2-digit",
                month: "long",
                year: "numeric"
            }
        ).format(date);
    }

    function compareDatesDescending(
        firstDate,
        secondDate
    ) {
        const firstTime =
            firstDate
                ? new Date(firstDate).getTime()
                : 0;

        const secondTime =
            secondDate
                ? new Date(secondDate).getTime()
                : 0;

        return secondTime - firstTime;
    }

    function formatEnumValue(value) {
        if (!value || typeof value !== "string") {
            return "";
        }

        return value
            .toLocaleLowerCase(window.I18n.getLocale())
            .split("_")
            .map(word =>
                word.charAt(0)
                    .toLocaleUpperCase(window.I18n.getLocale())
                + word.slice(1)
            )
            .join(" ");
    }

    function showMessage(
        container,
        message,
        isError = false
    ) {
        const messageElement =
            document.createElement("p");

        messageElement.className =
            isError
                ? "status-message error"
                : "status-message";

        messageElement.textContent = message;

        container.replaceChildren(
            messageElement
        );
    }
})();
