import React from 'react';
import Layout from '@theme/Layout';
import Link from '@docusaurus/Link';

export default function Home() {
  return (
    <Layout
      title="Raven Thunder"
      description="A powerful Java 17 + Vert.x platform for managing user journeys and nudges">
      <div className="hero" style={{
        background: 'linear-gradient(180deg, #f8f9fa 0%, #ffffff 100%)',
        borderBottom: '1px solid #e5e7eb',
        padding: '4rem 0',
        width: '100%',
        textAlign: 'center'
      }}>
        <div className="container" style={{
          maxWidth: '1200px',
          margin: '0 auto',
          padding: '0 3rem'
        }}>
          <h1 className="hero__title" style={{
            fontWeight: 700,
            fontSize: '3rem',
            letterSpacing: '-0.02em',
            color: '#1a1a1a',
            textAlign: 'center',
            marginBottom: '1rem'
          }}>
            ⚡ Raven Thunder ⚡
          </h1>
          <p className="hero__subtitle" style={{
            fontWeight: 400,
            fontSize: '1.25rem',
            color: '#6b7280',
            maxWidth: '700px',
            margin: '0 auto',
            textAlign: 'center',
            lineHeight: 1.6,
            marginBottom: '2rem'
          }}>
            A powerful Java 17 + Vert.x platform for managing user journeys and nudges
          </p>
          <div className="hero__buttons" style={{
            display: 'flex',
            gap: '1rem',
            justifyContent: 'center',
            flexWrap: 'wrap',
            marginTop: '2rem'
          }}>
            <Link
              className="button button--primary button--lg"
              to="/raven-thunder/getting-started/overview"
              style={{
                fontWeight: 500,
                padding: '0.75rem 1.5rem',
                borderRadius: '8px',
                backgroundColor: '#0A84FF',
                color: 'white',
                border: 'none',
                textDecoration: 'none',
                display: 'inline-block'
              }}>
              Get Started
            </Link>
            <Link
              className="button button--secondary button--lg"
              href="https://github.com/dream-horizon-org/thunder"
              style={{
                fontWeight: 500,
                padding: '0.75rem 1.5rem',
                borderRadius: '8px',
                backgroundColor: 'transparent',
                color: '#0A84FF',
                border: '1px solid #0A84FF',
                textDecoration: 'none'
              }}>
              View on GitHub
            </Link>
          </div>
        </div>
      </div>

      <div className="container" style={{
        maxWidth: '1200px',
        margin: '0 auto',
        padding: '3rem',
        textAlign: 'center'
      }}>
        <h2 style={{
          textAlign: 'center',
          margin: '3rem 0 2rem 0',
          fontWeight: 600,
          fontSize: '1.75rem',
          color: '#1a1a1a'
        }}>Features</h2>

        <div className="row" style={{
          display: 'flex',
          flexWrap: 'wrap',
          margin: '0 -0.5rem',
          justifyContent: 'center',
          width: '100%'
        }}>
          {[
            {
              emoji: '🚀',
              title: 'Multi-Module Architecture',
              description: 'Clean separation with thunder-core, thunder-api, and thunder-admin modules.'
            },
            {
              emoji: '🔌',
              title: 'REST APIs',
              description: 'Complete Admin API (19 endpoints) and SDK/Debug APIs (7 endpoints) for managing CTA Journeys and Behaviour.'
            },
            {
              emoji: '💾',
              title: 'Aerospike Integration',
              description: 'Reactive data access with RxJava3, complete with Docker setup, seed data, and indexes for local development.'
            },
            {
              emoji: '🐳',
              title: 'Docker Ready',
              description: 'Full Docker Compose setup with Aerospike, automatic seed data, and pre-configured indexes.'
            },
            {
              emoji: '✅',
              title: 'Health Checks',
              description: 'Comprehensive health monitoring for services and Aerospike connectivity.'
            },
            {
              emoji: '🧪',
              title: 'Testing',
              description: 'CI with unit and integration tests, release as Docker images and fat JARs.'
            }
          ].map((feature, idx) => (
            <div key={idx} className="col col--4" style={{
              padding: '0 0.5rem',
              marginBottom: '2rem',
              flex: '0 0 33.333333%',
              maxWidth: '33.333333%',
              minWidth: '280px'
            }}>
              <div className="card" style={{
                borderRadius: '8px',
                border: '1px solid #e5e7eb',
                background: '#ffffff',
                boxShadow: '0 1px 3px rgba(0, 0, 0, 0.05)',
                height: '100%',
                transition: 'all 0.2s ease'
              }}>
                <div className="card__header" style={{
                  padding: '1.25rem 1.25rem 0.75rem 1.25rem'
                }}>
                  <h3 style={{
                    marginBottom: 0,
                    fontSize: '1.25rem',
                    fontWeight: 600,
                    color: '#1a1a1a'
                  }}>
                    {feature.emoji} {feature.title}
                  </h3>
                </div>
                <div className="card__body" style={{
                  padding: '0 1.25rem 1.25rem 1.25rem',
                  color: '#6b7280'
                }}>
                  <p>{feature.description}</p>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </Layout>
  );
}

